package gn.projet.cotisation.service.membre;

import gn.projet.cotisation.dto.mapper.PaiementMapper;
import gn.projet.cotisation.dto.mapper.PenaliteMapper;
import gn.projet.cotisation.dto.membre.MembreCotisationsResponseDTO;
import gn.projet.cotisation.dto.membre.MoisImpayeDTO;
import gn.projet.cotisation.entity.ConfigurationVillage;
import gn.projet.cotisation.entity.Paiement;
import gn.projet.cotisation.entity.Penalite;
import gn.projet.cotisation.entity.StatutPenalite;
import gn.projet.cotisation.entity.Utilisateur;
import gn.projet.cotisation.entity.Village;
import gn.projet.cotisation.exception.utilisateur.UtilisateurSansVillageException;
import gn.projet.cotisation.repository.configurationVillage.ConfigurationVillageRepository;
import gn.projet.cotisation.repository.paiement.PaiementRepository;
import gn.projet.cotisation.repository.penalite.PenaliteRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MembreCotisationServiceImpl implements MembreCotisationService {

    private final PaiementRepository paiementRepository;
    private final PenaliteRepository penaliteRepository;
    private final ConfigurationVillageRepository configurationVillageRepository;

    public MembreCotisationServiceImpl(
            PaiementRepository paiementRepository,
            PenaliteRepository penaliteRepository,
            ConfigurationVillageRepository configurationVillageRepository
    ) {
        this.paiementRepository = paiementRepository;
        this.penaliteRepository = penaliteRepository;
        this.configurationVillageRepository = configurationVillageRepository;
    }

    @Override
    public MembreCotisationsResponseDTO getMesCotisations(Utilisateur membreConnecte, Integer anneeDemandee) {
        Village village = membreConnecte.getVillage();
        if (village == null) {
            throw new UtilisateurSansVillageException(membreConnecte.getId());
        }

        int annee = anneeDemandee != null ? anneeDemandee : LocalDate.now().getYear();

        BigDecimal montantCotisationMensuelle = configurationVillageRepository.findByVillageId(village.getId())
                .map(ConfigurationVillage::getMontantCotisationMensuelle)
                .orElse(null);

        List<Paiement> mesPaiements = paiementRepository.findByUtilisateurId(membreConnecte.getId());

        List<Paiement> paiementsTriesDescendant = mesPaiements.stream()
                .sorted(Comparator.comparing(Paiement::getAnnee)
                        .thenComparing(Paiement::getMois)
                        .reversed())
                .toList();

        BigDecimal montantTotalPaye = mesPaiements.stream()
                .map(Paiement::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<MoisImpayeDTO> moisImpayes = calculerMoisImpayes(mesPaiements, montantCotisationMensuelle, annee);

        BigDecimal montantTotalDu = moisImpayes.stream()
                .map(MoisImpayeDTO::montant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Penalite> mesPenalites = penaliteRepository.findByUtilisateurId(membreConnecte.getId());

        List<Penalite> penalitesTrieesDescendant = mesPenalites.stream()
                .sorted(Comparator.comparing(Penalite::getDate).reversed())
                .toList();

        BigDecimal montantTotalPenalitesNonPayees = mesPenalites.stream()
                .filter(p -> p.getStatut() == StatutPenalite.NON_PAYEE)
                .map(Penalite::getMontant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new MembreCotisationsResponseDTO(
                montantCotisationMensuelle,
                paiementsTriesDescendant.stream().map(PaiementMapper::toResponseDto).toList(),
                montantTotalPaye,
                moisImpayes,
                montantTotalDu,
                penalitesTrieesDescendant.stream().map(PenaliteMapper::toResponseDto).toList(),
                montantTotalPenalitesNonPayees
        );
    }

    // Mois de l'année demandée déjà entamés (passés ou en cours) sans Paiement correspondant.
    // "déjà entamé" = le mois n'est pas dans le futur par rapport à aujourd'hui (YearMonth.now()) :
    // pour une année passée, les 12 mois comptent ; pour l'année courante, seulement jusqu'au mois
    // courant inclus ; pour une année future, aucun (rien n'est encore dû).
    private List<MoisImpayeDTO> calculerMoisImpayes(List<Paiement> paiements, BigDecimal montantCotisationMensuelle, int annee) {
        if (montantCotisationMensuelle == null) {
            return List.of();
        }

        YearMonth maintenant = YearMonth.now();

        Set<Integer> moisPayesCetteAnnee = paiements.stream()
                .filter(p -> p.getAnnee() == annee)
                .map(Paiement::getMois)
                .collect(Collectors.toSet());

        List<MoisImpayeDTO> moisImpayes = new ArrayList<>();
        for (int mois = 1; mois <= 12; mois++) {
            if (YearMonth.of(annee, mois).isAfter(maintenant)) {
                break; // mois futurs : rien de plus à ajouter, les suivants le sont aussi
            }
            if (!moisPayesCetteAnnee.contains(mois)) {
                moisImpayes.add(new MoisImpayeDTO(mois, annee, montantCotisationMensuelle));
            }
        }
        return moisImpayes;
    }
}
