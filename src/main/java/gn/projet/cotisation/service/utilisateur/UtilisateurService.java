package gn.projet.cotisation.service.utilisateur; // sous-package "utilisateur" du package service

import gn.projet.cotisation.dto.utilisateurs.CreateMembreRequestDTO; // DTO reçu en entrée pour créer un membre
import gn.projet.cotisation.dto.utilisateurs.UpdateUtilisateurRequestDTO; // DTO reçu en entrée
import gn.projet.cotisation.dto.utilisateurs.UtilisateurResponseDTO; // DTO renvoyé en sortie
import gn.projet.cotisation.entity.Utilisateur; // entité du président connecté, passée par le contrôleur

import java.util.List;

// Interface = le CONTRAT du service Utilisateur, sans détail d'implémentation.
public interface UtilisateurService {

    List<UtilisateurResponseDTO> findAll(); // renvoie tous les utilisateurs

    UtilisateurResponseDTO findById(Long id); // lève UtilisateurNotFoundException si absent

    UtilisateurResponseDTO create(UpdateUtilisateurRequestDTO dto); // lève TelephoneDejaUtiliseException si téléphone pris

    UtilisateurResponseDTO update(Long id, UpdateUtilisateurRequestDTO dto); // lève les deux exceptions ci-dessus

    void delete(Long id); // lève UtilisateurNotFoundException si absent

    // Crée un membre DANS un village précis, pour le compte du président actuellement connecté.
    // presidentConnecte : passé par le contrôleur (extrait du token via @AuthenticationPrincipal),
    // sert à vérifier que ce président gère bien villageId avant toute création.
    // Lève VillageNotFoundException (village inexistant), VillageAccessDeniedException (village pas géré
    // par ce président), ou TelephoneDejaUtiliseException (téléphone déjà pris).
    UtilisateurResponseDTO createMembre(Long villageId, CreateMembreRequestDTO dto, Utilisateur presidentConnecte);

    // Liste les membres d'un village précis, pour le compte du président actuellement connecté.
    // Même vérification d'accès que createMembre() : village inexistant -> VillageNotFoundException,
    // village pas géré par presidentConnecte -> VillageAccessDeniedException.
    List<UtilisateurResponseDTO> findMembresDuVIllage(Long villageId, Utilisateur presidentConnecter);

}
