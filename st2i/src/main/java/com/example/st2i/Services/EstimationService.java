package com.example.st2i.Services;

import com.example.st2i.DTO.EstimationRequest;
import com.example.st2i.DTO.EstimationResponse;
import com.example.st2i.Entities.Estimation;
import com.example.st2i.Entities.Projet;
import com.example.st2i.Entities.TCC;
import com.example.st2i.Entities.User;
import com.example.st2i.Repositories.EstimationRepository;
import com.example.st2i.Repositories.ProjetRepository;
import com.example.st2i.Repositories.TCCRepository;
import com.example.st2i.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EstimationService {

    @Autowired private EstimationRepository estimationRepository;
    @Autowired private ProjetRepository projetRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private TCCRepository tccRepository;

    public EstimationResponse create(EstimationRequest request) {
        Projet projet = projetRepository.findById(request.getProjetId())
                .orElseThrow(() -> new RuntimeException("Projet non trouvé"));

        User ressource = userRepository.findById(request.getRessourceId())
                .orElseThrow(() -> new RuntimeException("Ressource non trouvée"));

        double nbrJours = request.getNbrJours() != null ? request.getNbrJours() : 0;


        double tarifHJ    = calculerTarifHJ(ressource);
        double coutEstime = nbrJours * tarifHJ;

        Estimation estimation = Estimation.builder()
                .projet(projet)
                .ressource(ressource)
                .nbrJours(nbrJours)
                .tarifHJ(tarifHJ)
                .coutEstime(coutEstime)
                .build();

        return toDTO(estimationRepository.save(estimation));
    }

    public EstimationResponse recalculer(Long id) {
        Estimation existing = estimationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estimation non trouvée"));

        double nbrJours   = existing.getNbrJours() != null ? existing.getNbrJours() : 0;
        double tarifHJ    = calculerTarifHJ(existing.getRessource());
        double coutEstime = nbrJours * tarifHJ;

        existing.setTarifHJ(tarifHJ);
        existing.setCoutEstime(coutEstime);

        return toDTO(estimationRepository.save(existing));
    }

    public void recalculerToutesEstimations(Long projetId) {
        List<Estimation> estimations = estimationRepository.findByProjetId(projetId);
        for (Estimation e : estimations) {
            double nbrJours = e.getNbrJours() != null ? e.getNbrJours() : 0;
            double tarifHJ  = calculerTarifHJ(e.getRessource());
            e.setTarifHJ(tarifHJ);
            e.setCoutEstime(nbrJours * tarifHJ);
            estimationRepository.save(e);
        }
    }

    public EstimationResponse createFromAffectation(Long projetId, Long userId) {
        Projet projet = projetRepository.findById(projetId)
                .orElseThrow(() -> new RuntimeException("Projet non trouvé"));

        User ressource = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Ressource non trouvée"));

        boolean exists = estimationRepository
                .findByProjetId(projetId)
                .stream()
                .anyMatch(e -> e.getRessource().getId().equals(userId));

        if (exists) {
            return estimationRepository.findByProjetId(projetId)
                    .stream()
                    .filter(e -> e.getRessource().getId().equals(userId))
                    .map(this::toDTO)
                    .findFirst()
                    .orElseThrow();
        }

        double nbrJours   = calculerNbrJours(projet);
        double tarifHJ    = calculerTarifHJ(ressource);
        double coutEstime = nbrJours * tarifHJ;

        Estimation estimation = Estimation.builder()
                .projet(projet)
                .ressource(ressource)
                .nbrJours(nbrJours)
                .tarifHJ(tarifHJ)
                .coutEstime(coutEstime)
                .build();

        return toDTO(estimationRepository.save(estimation));
    }

    public List<EstimationResponse> getByProjet(Long projetId) {
        return estimationRepository.findByProjetId(projetId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public Double getTotalCoutEstime(Long projetId) {
        return estimationRepository.findByProjetId(projetId)
                .stream()
                .mapToDouble(e -> e.getCoutEstime() != null ? e.getCoutEstime() : 0)
                .sum();
    }

    public EstimationResponse update(Long id, EstimationRequest request) {
        Estimation existing = estimationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Estimation non trouvée"));

        if (request.getNbrJours() != null) {
            existing.setNbrJours(request.getNbrJours());
        }
        if (request.getTarifHJ() != null) {
            existing.setTarifHJ(request.getTarifHJ());
        }

        double nbrJours = existing.getNbrJours() != null ? existing.getNbrJours() : 0;
        double tarifHJ  = existing.getTarifHJ()  != null ? existing.getTarifHJ()  : 0;
        existing.setCoutEstime(nbrJours * tarifHJ);

        return toDTO(estimationRepository.save(existing));
    }

    public void delete(Long id) {
        estimationRepository.deleteById(id);
    }


    private double calculerNbrJours(Projet projet) {
        if (projet.getDateDemarrageEffective() != null && projet.getDateFinPrevu() != null) {
            return ChronoUnit.DAYS.between(
                    projet.getDateDemarrageEffective(),
                    projet.getDateFinPrevu()
            );
        }
        if (projet.getDateDemarrage() != null && projet.getDateFinPrevu() != null) {
            return ChronoUnit.DAYS.between(
                    projet.getDateDemarrage(),
                    projet.getDateFinPrevu()
            );
        }
        return 0;
    }

    private double calculerTarifHJ(User ressource) {
        Optional<TCC> tcc = tccRepository
                .findTopByUtilisateurIdOrderByAnneeDesc(ressource.getId());

        if (tcc.isEmpty()) return 0;

        double tccBase   = tcc.get().getTccBase()   != null ? tcc.get().getTccBase()   : 0;
        double tccAvecFG = tcc.get().getTccAvecFG() != null ? tcc.get().getTccAvecFG() : 0;

        return (tccBase + tccAvecFG) / 264.0;
    }

    private EstimationResponse toDTO(Estimation e) {
        return new EstimationResponse(
                e.getId(),
                e.getNbrJours(),
                e.getTarifHJ(),
                e.getCoutEstime(),
                new EstimationResponse.ProjetSummary(
                        e.getProjet().getId(),
                        e.getProjet().getTitre()
                ),
                new EstimationResponse.RessourceSummary(
                        e.getRessource().getId(),
                        e.getRessource().getNom(),
                        e.getRessource().getPrenom()
                )
        );
    }
}