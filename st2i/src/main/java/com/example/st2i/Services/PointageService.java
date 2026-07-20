package com.example.st2i.Services;

import com.example.st2i.DTO.PointageRequest;
import com.example.st2i.DTO.PointageResponse;
import com.example.st2i.Entities.CoutReel;
import com.example.st2i.Entities.Pointage;
import com.example.st2i.Entities.Projet;
import com.example.st2i.Entities.TCC;
import com.example.st2i.Entities.User;
import com.example.st2i.Repositories.CoutReelRepository;
import com.example.st2i.Repositories.PointageRepository;
import com.example.st2i.Repositories.ProjetRepository;
import com.example.st2i.Repositories.TCCRepository;
import com.example.st2i.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PointageService {

    @Autowired private PointageRepository  pointageRepository;
    @Autowired private ProjetRepository   projetRepository;
    @Autowired private UserRepository     userRepository;
    @Autowired private TCCRepository      tccRepository;
    @Autowired private CoutReelService    coutReelService;
    @Autowired private CoutReelRepository coutReelRepository;

    // ✅ Ressource pointe son temps (upsert par mois)
    public PointageResponse pointer(Long ressourceId, PointageRequest request) {
        Projet projet = projetRepository.findById(request.getProjetId())
                .orElseThrow(() -> new RuntimeException("Projet non trouvé"));

        User ressource = userRepository.findById(ressourceId)
                .orElseThrow(() -> new RuntimeException("Ressource non trouvée"));

        LocalDate mois = LocalDate.parse(request.getMois() + "-01");

        double tarifHJ  = calculerTarifHJ(ressource, mois.getYear());
        double nbrJours = request.getNbrJoursReel() != null ? request.getNbrJoursReel() : 0;
        double coutReel = nbrJours * tarifHJ;

        Pointage pointage = pointageRepository
                .findByProjetIdAndRessourceIdAndMois(projet.getId(), ressourceId, mois)
                .orElse(Pointage.builder()
                        .projet(projet)
                        .ressource(ressource)
                        .mois(mois)
                        .statut("SAISI")
                        .build());

        pointage.setNbrJoursReel(nbrJours);
        pointage.setTarifHJ(tarifHJ);
        pointage.setCoutReel(coutReel);
        pointage.setCommentaire(request.getCommentaire());

        Pointage saved = pointageRepository.save(pointage);

        // ✅ Recalculer CoutReel automatiquement depuis ce pointage
        coutReelService.recalculerDepuisPointage(saved);

        return toDTO(saved);
    }

    // ✅ Pointer plusieurs mois d'un coup — remplace intégralement les anciens pointages
    @Transactional
    public List<PointageResponse> pointerBatch(Long ressourceId, List<PointageRequest> requests) {
        if (requests.isEmpty()) return List.of();

        Long projetId = requests.get(0).getProjetId();

        // Supprimer les anciens pointages de ce user pour ce projet
        List<Pointage> anciens = pointageRepository
                .findByProjetIdAndRessourceIdOrderByMois(projetId, ressourceId);
        if (!anciens.isEmpty()) {
            pointageRepository.deleteAll(anciens);
            // Supprimer aussi les CoutReel dérivés afin qu'il n'en reste pas d'orphelins
            List<CoutReel> anciensCoutReel = coutReelRepository
                    .findByProjetIdAndRessourceIdOrderByMois(projetId, ressourceId);
            coutReelRepository.deleteAll(anciensCoutReel);
        }

        // Recréer uniquement les mois avec des jours > 0
        return requests.stream()
                .filter(r -> r.getNbrJoursReel() != null && r.getNbrJoursReel() > 0)
                .map(r -> pointer(ressourceId, r))
                .collect(Collectors.toList());
    }


    // ✅ Mes pointages sur un projet
    public List<PointageResponse> getMesPointages(Long ressourceId, Long projetId) {
        return pointageRepository
                .findByProjetIdAndRessourceIdOrderByMois(projetId, ressourceId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ✅ Tous les pointages d'un projet (vue admin)
    public List<PointageResponse> getByProjet(Long projetId) {
        return pointageRepository.findByProjetIdOrderByMois(projetId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ✅ Tous mes pointages (toutes projets)
    public List<PointageResponse> getMesPointagesAll(Long ressourceId) {
        return pointageRepository.findByRessourceIdOrderByMois(ressourceId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public void delete(Long id) { pointageRepository.deleteById(id); }

    private double calculerTarifHJ(User ressource, int year) {
        List<TCC> tccList = tccRepository.findByUtilisateurIdAndYear(ressource.getId(), year);
        Optional<TCC> tcc = tccList.isEmpty()
                ? tccRepository.findTopByUtilisateurIdOrderByAnneeDesc(ressource.getId())
                : Optional.of(tccList.get(0));
        if (tcc.isEmpty()) return 0;
        double base = tcc.get().getTccBase()   != null ? tcc.get().getTccBase()   : 0;
        double fg   = tcc.get().getTccAvecFG() != null ? tcc.get().getTccAvecFG() : 0;
        return (base + fg) / 264.0;
    }

    private PointageResponse toDTO(Pointage p) {
        return new PointageResponse(
                p.getId(),
                p.getMois() != null ? p.getMois().toString().substring(0, 7) : null,
                p.getNbrJoursReel(),
                p.getTarifHJ(),
                p.getCoutReel(),
                p.getStatut(),
                p.getCommentaire(),
                new PointageResponse.RessourceSummary(
                        p.getRessource().getId(),
                        p.getRessource().getNom(),
                        p.getRessource().getPrenom()
                ),
                new PointageResponse.ProjetSummary(
                        p.getProjet().getId(),
                        p.getProjet().getTitre()
                )
        );
    }
}