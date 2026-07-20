package com.example.st2i.Services;

import com.example.st2i.DTO.CoutPrevRequest;
import com.example.st2i.DTO.CoutPrevResponse;
import com.example.st2i.Entities.CoutPrev;
import com.example.st2i.Entities.Projet;
import com.example.st2i.Entities.TCC;
import com.example.st2i.Entities.User;
import com.example.st2i.Repositories.CoutPrevRepository;
import com.example.st2i.Repositories.ProjetRepository;
import com.example.st2i.Repositories.TCCRepository;
import com.example.st2i.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CoutPrevService {

    @Autowired private CoutPrevRepository coutPrevRepository;
    @Autowired private ProjetRepository   projetRepository;
    @Autowired private UserRepository     userRepository;
    @Autowired private TCCRepository      tccRepository;

    // ── Créer ──
    public CoutPrevResponse create(CoutPrevRequest request) {
        Projet projet = projetRepository.findById(request.getProjetId())
                .orElseThrow(() -> new RuntimeException("Projet non trouvé"));

        User ressource = userRepository.findById(request.getRessourceId())
                .orElseThrow(() -> new RuntimeException("Ressource non trouvée"));

        int moisYear        = LocalDate.parse(request.getMois() + "-01").getYear();
        double tarifHJ      = calculerTarifHJ(ressource, moisYear);
        double chargePrevuM = request.getChargePrevuM();
        double coutPrev     = chargePrevuM * tarifHJ;
        double nbrJours     = calculerNbrJoursTotaux(
                request.getProjetId(), request.getRessourceId(), chargePrevuM
        );

        CoutPrev entity = CoutPrev.builder()
                .projet(projet)
                .ressource(ressource)
                .mois(LocalDate.parse(request.getMois() + "-01"))
                .chargePrevuM(chargePrevuM)
                .coutPrev(coutPrev)
                .nbrJours(nbrJours)
                .dateDemarrageEffective(
                        projet.getDateDemarrageEffective() != null
                                ? projet.getDateDemarrageEffective()
                                : projet.getDateDemarrage()
                )
                .dateFinPrevu(projet.getDateFinPrevu())
                .build();

        return toDTO(coutPrevRepository.save(entity));
    }

    // ── Modifier ──
    public CoutPrevResponse update(Long id, CoutPrevRequest request) {
        CoutPrev existing = coutPrevRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coût prévisionnel non trouvé"));

        int moisYear        = LocalDate.parse(request.getMois() + "-01").getYear();
        double tarifHJ      = calculerTarifHJ(existing.getRessource(), moisYear);
        double chargePrevuM = request.getChargePrevuM();
        double coutPrev     = chargePrevuM * tarifHJ;

        // Recalculer nbrJours sans compter l'ancienne valeur
        double totalSansActuel = coutPrevRepository
                .findByProjetIdAndRessourceId(
                        existing.getProjet().getId(),
                        existing.getRessource().getId()
                )
                .stream()
                .filter(cp -> !cp.getId().equals(id))
                .mapToDouble(cp -> cp.getChargePrevuM() != null ? cp.getChargePrevuM() : 0)
                .sum();

        double nbrJours = totalSansActuel + chargePrevuM;

        existing.setMois(LocalDate.parse(request.getMois() + "-01"));
        existing.setChargePrevuM(chargePrevuM);
        existing.setCoutPrev(coutPrev);
        existing.setNbrJours(nbrJours);

        return toDTO(coutPrevRepository.save(existing));
    }

    // ── Liste par projet ──
    public List<CoutPrevResponse> getByProjet(Long projetId) {
        return coutPrevRepository.findByProjetId(projetId)
                .stream()
                .sorted(Comparator.comparing(CoutPrev::getMois))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ── Total coût prévisionnel ──
    public Double getTotalCoutPrev(Long projetId) {
        return coutPrevRepository.findByProjetId(projetId)
                .stream()
                .mapToDouble(cp -> cp.getCoutPrev() != null ? cp.getCoutPrev() : 0)
                .sum();
    }

    // ── Cumul restant = budget - total prévisionnel ──
    public Double getCumulRestant(Long projetId) {
        Projet projet = projetRepository.findById(projetId)
                .orElseThrow(() -> new RuntimeException("Projet non trouvé"));
        double budget = projet.getBudgetInitial() != null ? projet.getBudgetInitial() : 0;
        return budget - getTotalCoutPrev(projetId);
    }

    // ── Plan de charge par mois ──
    public Map<String, Map<String, Object>> getPlanDeCharge(Long projetId) {
        List<CoutPrev> liste = coutPrevRepository.findByProjetId(projetId);
        Map<String, Map<String, Object>> result = new LinkedHashMap<>();

        for (CoutPrev cp : liste) {
            String mois      = cp.getMois().toString().substring(0, 7);
            String ressource = cp.getRessource().getPrenom() + " "
                    + cp.getRessource().getNom();

            result.computeIfAbsent(mois, k -> new LinkedHashMap<>());
            result.get(mois).put(ressource + "_jours", cp.getChargePrevuM());
            result.get(mois).put(ressource + "_cout",  cp.getCoutPrev());
        }
        return result;
    }

    // ── Total par mois ──
    public Map<String, Double> getTotalParMois(Long projetId) {
        List<CoutPrev> liste = coutPrevRepository.findByProjetId(projetId);
        Map<String, Double> totaux = new LinkedHashMap<>();
        for (CoutPrev cp : liste) {
            String mois = cp.getMois().toString().substring(0, 7);
            totaux.merge(mois, cp.getCoutPrev() != null ? cp.getCoutPrev() : 0, Double::sum);
        }
        return totaux;
    }

    // ── Comparaison avec coûts réels ──
    public Map<String, Object> getComparaison(Long projetId) {
        double totalPrev = getTotalCoutPrev(projetId);
        double totalReel = 0; // sera rempli par CoutReelService

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalPrevisionnel", totalPrev);
        result.put("totalReel", totalReel);
        result.put("ecart", totalReel - totalPrev);
        result.put("ecartPourcentage",
                totalPrev > 0
                        ? Math.round((totalReel - totalPrev) / totalPrev * 100 * 100.0) / 100.0
                        : 0
        );
        result.put("statut", totalReel > totalPrev ? "DEPASSEMENT" : "DANS_BUDGET");
        return result;
    }

    // ── Supprimer ──
    public void delete(Long id) {
        coutPrevRepository.deleteById(id);
    }

    // ── Calculs privés ──
    private double calculerTarifHJ(User ressource, int year) {
        List<TCC> tccList = tccRepository.findByUtilisateurIdAndYear(ressource.getId(), year);
        Optional<TCC> tcc = tccList.isEmpty()
                ? tccRepository.findTopByUtilisateurIdOrderByAnneeDesc(ressource.getId())
                : Optional.of(tccList.get(0));
        if (tcc.isEmpty()) return 0;
        double tccBase   = tcc.get().getTccBase()   != null ? tcc.get().getTccBase()   : 0;
        double tccAvecFG = tcc.get().getTccAvecFG() != null ? tcc.get().getTccAvecFG() : 0;
        return (tccBase + tccAvecFG) / 264.0;
    }

    private double calculerNbrJoursTotaux(Long projetId, Long ressourceId, double newCharge) {
        double totalExistant = coutPrevRepository
                .findByProjetIdAndRessourceId(projetId, ressourceId)
                .stream()
                .mapToDouble(cp -> cp.getChargePrevuM() != null ? cp.getChargePrevuM() : 0)
                .sum();
        return totalExistant + newCharge;
    }

    private CoutPrevResponse toDTO(CoutPrev cp) {
        return new CoutPrevResponse(
                cp.getId(),
                cp.getMois() != null ? cp.getMois().toString().substring(0, 7) : null,
                cp.getChargePrevuM(),
                cp.getCoutPrev(),
                cp.getNbrJours(),
                cp.getDateDemarrageEffective() != null
                        ? cp.getDateDemarrageEffective().toString() : null,
                cp.getDateFinPrevu() != null
                        ? cp.getDateFinPrevu().toString() : null,
                new CoutPrevResponse.ProjetSummary(
                        cp.getProjet().getId(),
                        cp.getProjet().getTitre()
                ),
                new CoutPrevResponse.RessourceSummary(
                        cp.getRessource().getId(),
                        cp.getRessource().getNom(),
                        cp.getRessource().getPrenom()
                )
        );
    }

    public List<CoutPrevResponse> getByProjetAndUser(Long projetId, Long userId) {
        return coutPrevRepository
                .findByProjetIdAndRessourceId(projetId, userId)
                .stream()
                .sorted(Comparator.comparing(CoutPrev::getMois))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}