package com.example.st2i.Services;

import com.example.st2i.DTO.KpiMensuelRequest;
import com.example.st2i.Entities.*;
import com.example.st2i.Repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class KpiAutoCalculeService {

    @Autowired private ProjetRepository projetRepository;
    @Autowired private CoutPrevRepository coutPrevRepository;
    @Autowired private CoutReelRepository coutReelRepository;
    @Autowired private PointageRepository pointageRepository;
    @Autowired private EcheanceFacturationRepository echeanceRepository;
    @Autowired private LivrableRepository livrableRepository;
    @Autowired private EstimationRepository estimationRepository;
    @Autowired private TCCRepository tccRepository;

    public KpiMensuelRequest calculerKpiPourMois(Long projetId, String moisStr) {

        Projet projet = projetRepository.findById(projetId)
                .orElseThrow(() -> new RuntimeException("Projet non trouvé"));

        LocalDate mois      = LocalDate.parse(moisStr + "-01");
        LocalDate debutMois = mois.withDayOfMonth(1);
        LocalDate finMois   = mois.withDayOfMonth(mois.lengthOfMonth());

        KpiMensuelRequest kpi = new KpiMensuelRequest();
        kpi.setProjetId(projetId);
        kpi.setMois(moisStr);

        // ══════════════════════════════════════════
        // 1. CONSOMMATION DÉLAIS
        // ══════════════════════════════════════════
        LocalDate dateDebut = projet.getDateDemarrageEffective() != null
                ? projet.getDateDemarrageEffective()
                : projet.getDateDemarrage();
        LocalDate dateFin = projet.getDateFinPrevu();

        if (dateDebut != null && dateFin != null) {
            long dureeTotal   = ChronoUnit.DAYS.between(dateDebut, dateFin);
            // ✅ Utiliser fin du mois pour une meilleure précision
            long joursEcoules = ChronoUnit.DAYS.between(dateDebut, finMois);
            if (dureeTotal > 0) {
                double conso = Math.min(1.0, Math.max(0.0,
                        (double) joursEcoules / dureeTotal));
                kpi.setConsommationDelais(round3(conso));
            } else {
                kpi.setConsommationDelais(0.0);
            }
        } else {
            kpi.setConsommationDelais(0.0);
        }

        // ══════════════════════════════════════════
        // 2. DELIVERY depuis Livrables
        // ══════════════════════════════════════════
        long totalLivrables = livrableRepository.countByProjetId(projetId);
        long livresCount    = livrableRepository.countByProjetIdAndLivreTrue(projetId);
        kpi.setDelivery(totalLivrables > 0
                ? round3((double) livresCount / totalLivrables)
                : 0.0);

        // ══════════════════════════════════════════
        // 3. CHARGE PRÉVUE ce mois depuis CoutPrev
        // ✅ Utiliser between pour éviter problèmes de format
        // ══════════════════════════════════════════
        List<CoutPrev> coutPrevsMois = coutPrevRepository
                .findByProjetIdAndMoisBetween(projetId, debutMois, finMois);
        double chargePrevueMois = coutPrevsMois.stream()
                .mapToDouble(c -> c.getChargePrevuM() != null ? c.getChargePrevuM() : 0)
                .sum();
        kpi.setChargePrevueMois(chargePrevueMois);

        // ══════════════════════════════════════════
        // 4. CHARGE CONSOMMÉE ce mois depuis Pointages
        // ✅ Utiliser between
        // ══════════════════════════════════════════
        List<Pointage> pointagesMois = pointageRepository
                .findByProjetIdAndMoisBetween(projetId, debutMois, finMois);
        double chargeConsommeeMois = pointagesMois.stream()
                .mapToDouble(p -> p.getNbrJoursReel() != null ? p.getNbrJoursReel() : 0)
                .sum();
        kpi.setChargeConsommeeMois(chargeConsommeeMois);

        // ══════════════════════════════════════════
        // 5. CUMUL CHARGES depuis tous pointages ≤ fin du mois
        // ══════════════════════════════════════════
        List<Pointage> tousPointages = pointageRepository
                .findByProjetIdAndMoisLessThanEqual(projetId, finMois);
        double chargeConsommeeCumul = tousPointages.stream()
                .mapToDouble(p -> p.getNbrJoursReel() != null ? p.getNbrJoursReel() : 0)
                .sum();
        kpi.setChargeConsommeeCumul(chargeConsommeeCumul);

        // ══════════════════════════════════════════
        // 6. CHARGES VENDUES depuis Estimations (DI)
        // ══════════════════════════════════════════
        List<Estimation> estimations = estimationRepository.findByProjetId(projetId);
        double chargesVendues = estimations.stream()
                .mapToDouble(e -> e.getNbrJours() != null ? e.getNbrJours() : 0)
                .sum();

        // ══════════════════════════════════════════
        // 7. RAF ETC = Charges vendues - Cumulé réel
        // ══════════════════════════════════════════
        double rafETC = Math.max(0, chargesVendues - chargeConsommeeCumul);
        kpi.setResteAFaireETC(round1(rafETC));

        // ══════════════════════════════════════════
        // 8. DÉRIVE = Cumulé - (Charges vendues × Consommation délais)
        // ══════════════════════════════════════════
        double consDelais = kpi.getConsommationDelais() != null
                ? kpi.getConsommationDelais() : 0;
        double chargesAttendues = chargesVendues * consDelais;
        double derive = round1(chargeConsommeeCumul - chargesAttendues);
        kpi.setDerive(derive);

        kpi.setResteAFaireEAC(round1(rafETC + Math.max(0, derive)));


        List<CoutReel> coutsReelsMois = coutReelRepository
                .findByProjetIdAndMoisBetween(projetId, debutMois, finMois);
        double coutReelMois = coutsReelsMois.stream()
                .mapToDouble(c -> c.getCoutReel() != null ? c.getCoutReel() : 0)
                .sum();
        kpi.setCoutReel(round2(coutReelMois));


        List<CoutReel> tousCoutsReels = coutReelRepository
                .findByProjetIdAndMoisLessThanEqual(projetId, finMois);
        double coutReelCumul = tousCoutsReels.stream()
                .mapToDouble(c -> c.getCoutReel() != null ? c.getCoutReel() : 0)
                .sum();
        kpi.setCoutReelCumul(round2(coutReelCumul));


        double tarifHJMoyen = calculerTarifHJMoyen(projetId);
        double coutETC = round2(rafETC * tarifHJMoyen);
        kpi.setCoutETC(coutETC);
        kpi.setCoutEAC(round2(coutReelCumul + coutETC));


        List<EcheanceFacturation> echeancesEmises = echeanceRepository
                .findByProjetIdAndEmiseTrueAndDateReelleLessThanEqual(projetId, finMois);
        double caFacture = echeancesEmises.stream()
                .mapToDouble(e -> e.getMontantFacture() != null ? e.getMontantFacture() : 0)
                .sum();
        kpi.setCaFacture(round2(caFacture));


        double montantAffaire = projet.getMontantTotal() != null
                ? projet.getMontantTotal() : 0;
        if (montantAffaire > 0) {
            kpi.setTauxFacturation(round3(caFacture / montantAffaire));
        } else {
            kpi.setTauxFacturation(0.0);
        }


        kpi.setEarnedValue(0.0);
        kpi.setCaProduction(0.0);
        kpi.setStock(0.0);
        kpi.setMargeNetteActuelle(0.0);


        double coutEAC = kpi.getCoutEAC() != null ? kpi.getCoutEAC() : 0;
        if (montantAffaire > 0) {
            double margeEAC = montantAffaire - coutEAC;
            kpi.setMargeNetteEAC(round2(margeEAC));
            kpi.setMargeNetteEACPct(round3(margeEAC / montantAffaire));
        } else {
            kpi.setMargeNetteEAC(0.0);
            kpi.setMargeNetteEACPct(0.0);
        }

        return kpi;
    }

    public KpiMensuelRequest recalculerAvecEV(Long projetId,
                                              String moisStr,
                                              Double earnedValue) {
        double ev = earnedValue != null ? earnedValue : 0;
        if (ev > 1) ev = ev / 100.0;

        KpiMensuelRequest kpi = calculerKpiPourMois(projetId, moisStr);
        kpi.setEarnedValue(ev);

        Projet projet = projetRepository.findById(projetId).orElseThrow();
        double montantAffaire = projet.getMontantTotal() != null
                ? projet.getMontantTotal() : 0;

        // CA Production = Montant affaire × EV%
        double caProduction = round2(montantAffaire * ev);
        kpi.setCaProduction(caProduction);

        // Stock FAE = CA Production - CA Facturé
        double caFacture = kpi.getCaFacture() != null ? kpi.getCaFacture() : 0;
        kpi.setStock(round2(caProduction - caFacture));

        // Marge nette actuelle = CA Production - Coût cumulé
        double coutCumul = kpi.getCoutReelCumul() != null
                ? kpi.getCoutReelCumul() : 0;
        kpi.setMargeNetteActuelle(round2(caProduction - coutCumul));

        return kpi;
    }

    private double calculerTarifHJMoyen(Long projetId) {
        List<Pointage> pointages = pointageRepository.findByProjetId(projetId);
        if (pointages.isEmpty()) {
            List<Estimation> estimations = estimationRepository.findByProjetId(projetId);
            if (!estimations.isEmpty()) {
                return estimations.stream()
                        .filter(e -> e.getTarifHJ() != null && e.getTarifHJ() > 0)
                        .mapToDouble(Estimation::getTarifHJ)
                        .average()
                        .orElse(0);
            }
            return 0;
        }

        List<Long> ressourceIds = pointages.stream()
                .map(p -> p.getRessource().getId())
                .distinct()
                .collect(java.util.stream.Collectors.toList());

        double sommeTarifs = 0;
        int count = 0;

        for (Long ressourceId : ressourceIds) {
            java.util.Optional<TCC> tcc = tccRepository
                    .findTopByUtilisateurIdOrderByAnneeDesc(ressourceId);
            if (tcc.isPresent()) {
                double base = tcc.get().getTccBase()   != null ? tcc.get().getTccBase()   : 0;
                double fg   = tcc.get().getTccAvecFG() != null ? tcc.get().getTccAvecFG() : 0;
                sommeTarifs += (base + fg) / 264.0;
                count++;
            }
        }

        return count > 0 ? sommeTarifs / count : 0;
    }

    private double round1(double v) { return Math.round(v * 10.0)    / 10.0; }
    private double round2(double v) { return Math.round(v * 100.0)   / 100.0; }
    private double round3(double v) { return Math.round(v * 1000.0)  / 1000.0; }
}