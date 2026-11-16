package gn.projet.cotisation.service.typemouvementcaisse;

import gn.projet.cotisation.dto.mapper.TypeMouvementCaisseMapper;
import gn.projet.cotisation.dto.typemouvementcaisse.TypeMouvementCaisseResponseDTO;
import gn.projet.cotisation.dto.typemouvementcaisse.UpdateTypeMouvementCaisseRequestDTO;
import gn.projet.cotisation.entity.TypeMouvementCaisse;
import gn.projet.cotisation.entity.Utilisateur;
import gn.projet.cotisation.entity.Village;
import gn.projet.cotisation.exception.typemouvementcaisse.TypeMouvementCaisseDejaExistantException;
import gn.projet.cotisation.exception.typemouvementcaisse.TypeMouvementCaisseNotFoundException;
import gn.projet.cotisation.repository.typemouvementcaisse.TypeMouvementCaisseRepository;
import gn.projet.cotisation.service.village.VillageAccessService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TypeMouvementCaisseServiceImpl implements TypeMouvementCaisseService {

    private final TypeMouvementCaisseRepository typeMouvementCaisseRepository;
    private final VillageAccessService villageAccessService;

    public TypeMouvementCaisseServiceImpl(
            TypeMouvementCaisseRepository typeMouvementCaisseRepository,
            VillageAccessService villageAccessService
    ) {
        this.typeMouvementCaisseRepository = typeMouvementCaisseRepository;
        this.villageAccessService = villageAccessService;
    }

    @Override
    public List<TypeMouvementCaisseResponseDTO> findAllByVillage(Long villageId, Utilisateur presidentConnecte) {
        villageAccessService.verifierEtRecupererVillageGere(villageId, presidentConnecte);
        return typeMouvementCaisseRepository.findByVillageId(villageId).stream()
                .map(TypeMouvementCaisseMapper::toResponseDto)
                .toList();
    }

    @Override
    public TypeMouvementCaisseResponseDTO create(Long villageId, UpdateTypeMouvementCaisseRequestDTO dto, Utilisateur presidentConnecte) {
        Village village = villageAccessService.verifierEtRecupererVillageGere(villageId, presidentConnecte);

        if (typeMouvementCaisseRepository.existsByVillage_IdAndLibelleIgnoreCase(villageId, dto.libelle())) {
            throw new TypeMouvementCaisseDejaExistantException(dto.libelle());
        }

        TypeMouvementCaisse type = new TypeMouvementCaisse();
        type.setVillage(village);
        type.setLibelle(dto.libelle());
        return TypeMouvementCaisseMapper.toResponseDto(typeMouvementCaisseRepository.save(type));
    }

    @Override
    public TypeMouvementCaisseResponseDTO update(Long villageId, Long typeId, UpdateTypeMouvementCaisseRequestDTO dto, Utilisateur presidentConnecte) {
        villageAccessService.verifierEtRecupererVillageGere(villageId, presidentConnecte);

        TypeMouvementCaisse type = typeMouvementCaisseRepository.findById(typeId)
                .filter(t -> t.getVillage().getId().equals(villageId))
                .orElseThrow(() -> new TypeMouvementCaisseNotFoundException(typeId));

        // Ne vérifie le doublon que si le libellé change réellement : sinon un simple "update sans
        // changement" se heurterait à son propre libellé existant.
        if (!type.getLibelle().equalsIgnoreCase(dto.libelle())
                && typeMouvementCaisseRepository.existsByVillage_IdAndLibelleIgnoreCase(villageId, dto.libelle())) {
            throw new TypeMouvementCaisseDejaExistantException(dto.libelle());
        }

        type.setLibelle(dto.libelle());
        return TypeMouvementCaisseMapper.toResponseDto(typeMouvementCaisseRepository.save(type));
    }

    @Override
    public void delete(Long villageId, Long typeId, Utilisateur presidentConnecte) {
        villageAccessService.verifierEtRecupererVillageGere(villageId, presidentConnecte);

        TypeMouvementCaisse type = typeMouvementCaisseRepository.findById(typeId)
                .filter(t -> t.getVillage().getId().equals(villageId))
                .orElseThrow(() -> new TypeMouvementCaisseNotFoundException(typeId));

        // Si des dépenses référencent encore ce type, la contrainte de clé étrangère lève une
        // DataIntegrityViolationException, déjà traduite en 409 par GlobalExceptionHandler.
        typeMouvementCaisseRepository.delete(type);
    }
}
