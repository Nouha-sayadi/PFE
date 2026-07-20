package com.example.st2i.Services;

import com.example.st2i.DTO.CoutReelResponse;
import com.example.st2i.Entities.CoutReel;
import com.example.st2i.Entities.Pointage;
import com.example.st2i.Entities.Projet;
import com.example.st2i.Entities.TCC;
import com.example.st2i.Entities.User;
import com.example.st2i.Repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CoutReelService {

    @Autowired private CoutReelRepository coutReelRepository;
    @Autowired private ProjetRepository projetRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private PointageRepository pointageRepository;
    @Autowired private TCCRepository tccRepository;

    // ✅ Recalculer CoutReel depuis les Pointages pour un projet
    public void recalculerDepuisPointages(Long projetId) {
        // Récupérer tous les pointages du projet
        List<Pointage> pointages = pointageRepository.findByProjetIdOrderByMois(projetId);

        // Grouper par ressource
        pointages.stream()
                .collect(Collectors.groupingBy(p -> p.getRessource().getId()))
                .forEach((ressourceId, listePointages) -> {
                    User ressource = listePointages.get(0).getRessource();
                    Projet projet  = listePointages.get(0).getProjet();

                    // Pour chaque pointage → upsert CoutReel (TCC de l'année du mois)
                    listePointages.forEach(pointage -> {
                        LocalDate mois  = pointage.getMois();
                        double tarifHJ  = calculerTarifHJ(ressource, mois.getYear());
                        double nbrJours = pointage.getNbrJoursReel() != null ? pointage.getNbrJoursReel() : 0;
                        double coutReel = nbrJours * tarifHJ;

                        // Upsert — chercher si existe déjà
                        CoutReel cout = coutReelRepository
                                .findByProjetIdAndRessourceIdAndMois(projetId, ressourceId, mois)
                                .orElse(CoutReel.builder()
                                        .projet(projet)
                                        .ressource(ressource)
                                        .mois(mois)
                                        .build());

                        cout.setNbrJoursReel(nbrJours);
                        cout.setCoutReel(coutReel);
                        coutReelRepository.save(cout);
                    });
                });
    }

    // ✅ Recalculer pour un seul pointage (appelé après chaque pointage)
    public void recalculerDepuisPointage(Pointage pointage) {
        Long projetId    = pointage.getProjet().getId();
        Long ressourceId = pointage.getRessource().getId();
        LocalDate mois   = pointage.getMois();

        double tarifHJ  = calculerTarifHJ(pointage.getRessource(), pointage.getMois().getYear());
        double nbrJours = pointage.getNbrJoursReel() != null ? pointage.getNbrJoursReel() : 0;
        double coutReel = nbrJours * tarifHJ;

        CoutReel cout = coutReelRepository
                .findByProjetIdAndRessourceIdAndMois(projetId, ressourceId, mois)
                .orElse(CoutReel.builder()
                        .projet(pointage.getProjet())
                        .ressource(pointage.getRessource())
                        .mois(mois)
                        .build());

        cout.setNbrJoursReel(nbrJours);
        cout.setCoutReel(coutReel);
        coutReelRepository.save(cout);
    }

    public List<CoutReelResponse> getByProjet(Long projetId) {
        return coutReelRepository.findByProjetIdOrderByMois(projetId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<CoutReelResponse> getByProjetGroupeParRessource(Long projetId) {
        return getByProjet(projetId);
    }

    public Double getTotalByProjet(Long projetId) {
        return coutReelRepository.findByProjetId(projetId)
                .stream()
                .mapToDouble(c -> c.getCoutReel() != null ? c.getCoutReel() : 0)
                .sum();
    }

    public void delete(Long id) {
        coutReelRepository.findById(id).ifPresent(coutReel ->
            pointageRepository.findByProjetIdAndRessourceIdAndMois(
                    coutReel.getProjet().getId(),
                    coutReel.getRessource().getId(),
                    coutReel.getMois()
            ).ifPresent(pointageRepository::delete)
        );
        coutReelRepository.deleteById(id);
    }

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

    private CoutReelResponse toDTO(CoutReel c) {
        return new CoutReelResponse(
                c.getId(),
                c.getNbrJoursReel(),
                c.getMois() != null ? c.getMois().toString().substring(0, 7) : null,
                c.getDateDemarrage() != null ? c.getDateDemarrage().toString() : null,
                c.getDateFinReel()   != null ? c.getDateFinReel().toString()   : null,
                c.getCoutReel(),
                new CoutReelResponse.ProjetSummary(
                        c.getProjet().getId(),
                        c.getProjet().getTitre()
                ),
                new CoutReelResponse.RessourceSummary(
                        c.getRessource().getId(),
                        c.getRessource().getNom(),
                        c.getRessource().getPrenom()
                )
        );
    }
}