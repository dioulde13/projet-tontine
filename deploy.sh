
#!/usr/bin/env bash
set -Eeuo pipefail

APP_DIR="/opt/projet-tontine"
CONTAINER="projet-tontine"
IMAGE="projet-tontine:latest"
ROLLBACK_IMAGE="projet-tontine:rollback"
ENV_FILE="/root/projet-tontine.env"

cd "$APP_DIR"

echo "=== 1. Vérification de la configuration ==="

if [[ ! -f "$ENV_FILE" ]]; then
  echo "ERREUR : fichier d'environnement absent : $ENV_FILE"
  exit 1
fi

chmod 600 "$ENV_FILE"

BRANCH="$(git branch --show-current)"
if [[ -z "$BRANCH" ]]; then
  echo "ERREUR : impossible de déterminer la branche Git."
  exit 1
fi

echo "Branche : $BRANCH"

echo "=== 2. Récupération du code ==="
git pull --ff-only origin "$BRANCH"

echo "=== 3. Sauvegarde de l'image précédente ==="
if docker image inspect "$IMAGE" >/dev/null 2>&1; then
  docker image tag "$IMAGE" "$ROLLBACK_IMAGE"
else
  echo "Première image : aucun rollback disponible."
fi

echo "=== 4. Construction de la nouvelle image ==="
docker build -t "$IMAGE" .

echo "=== 5. Redémarrage du backend ==="
docker rm -f "$CONTAINER" >/dev/null 2>&1 || true

docker run -d \
  --name "$CONTAINER" \
  --network host \
  --restart unless-stopped \
  --env-file "$ENV_FILE" \
  "$IMAGE"

echo "=== 6. Vérification du démarrage ==="
for i in $(seq 1 30); do
  if ! docker inspect "$CONTAINER" \
      --format '{{.State.Running}}' 2>/dev/null | grep -q '^true$'; then
    echo "ERREUR : le nouveau conteneur s'est arrêté."
    break
  fi

  if docker logs "$CONTAINER" 2>&1 |
      grep -q 'Started CotisationApplication'; then
    echo "SUCCÈS : Spring Boot a démarré."
    docker ps --filter "name=^/${CONTAINER}$"
    echo "Consulter les logs : docker logs --tail 100 $CONTAINER"
    exit 0
  fi

  sleep 2
done

echo "ÉCHEC : le backend n'a pas confirmé son démarrage."
docker logs --tail 100 "$CONTAINER" || true

echo "=== Tentative de restauration ==="
if docker image inspect "$ROLLBACK_IMAGE" >/dev/null 2>&1; then
  docker rm -f "$CONTAINER" >/dev/null 2>&1 || true
  docker run -d \
    --name "$CONTAINER" \
    --network host \
    --restart unless-stopped \
    --env-file "$ENV_FILE" \
    "$ROLLBACK_IMAGE"

  echo "Ancienne image relancée. Vérifie ses logs."
else
  echo "Aucune ancienne image disponible pour la restauration."
fi

exit 1
