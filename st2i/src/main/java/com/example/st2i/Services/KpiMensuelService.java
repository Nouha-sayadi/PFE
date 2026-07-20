package com.example.st2i.Services;

import com.example.st2i.DTO.KpiMensuelRequest;
import com.example.st2i.Entities.KpiMensuelProjet;
import com.example.st2i.Entities.Projet;
import com.example.st2i.Repositories.KpiMensuelProjetRepository;
import com.example.st2i.Repositories.ProjetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class KpiMensuelService {

    @Autowired
    private KpiMensuelProjetRepository kpiRepository;
    @Autowired private ProjetRepository projetRepository;

    public KpiMensuelProjet saveOrUpdate(KpiMensuelRequest req) {
        if (req.getMois() == null || req.getMois().isBlank()) {
            throw new RuntimeException("Le mois est obligatoire");
        }
        Projet projet = projetRepository.findById(req.getProjetId())
                .orElseThrow(() -> new RuntimeException("Projet non trouvé"));

        LocalDate mois = LocalDate.parse(req.getMois() + "-01");

        KpiMensuelProjet kpi = kpiRepository
                .findByProjetIdAndMois(req.getProjetId(), mois)
                .orElse(KpiMensuelProjet.builder().projet(projet).mois(mois).build());

        kpi.setEarnedValue(req.getEarnedValue());
        kpi.setDelivery(req.getDelivery());
        kpi.setConsommationDelais(req.getConsommationDelais());
        kpi.setTauxFacturation(req.getTauxFacturation());
        kpi.setChargePrevueMois(req.getChargePrevueMois());
        kpi.setChargeConsommeeMois(req.getChargeConsommeeMois());
        kpi.setChargeConsommeeCumul(req.getChargeConsommeeCumul());
        kpi.setResteAFaireETC(req.getResteAFaireETC());
        kpi.setResteAFaireEAC(req.getResteAFaireEAC());
        kpi.setDerive(req.getDerive());
        kpi.setCoutReel(req.getCoutReel());
        kpi.setCoutReelCumul(req.getCoutReelCumul());
        kpi.setCoutETC(req.getCoutETC());
        kpi.setCoutEAC(req.getCoutEAC());
        kpi.setCaProduction(req.getCaProduction());
        kpi.setCaFacture(req.getCaFacture());
        kpi.setStock(req.getStock());
        kpi.setMargeNetteMois(req.getMargeNetteMois());
        kpi.setMargeNetteActuelle(req.getMargeNetteActuelle());
        kpi.setMargeNetteEAC(req.getMargeNetteEAC());
        kpi.setMargeNetteEACPct(req.getMargeNetteEACPct());
        kpi.setFaitsMarquants(req.getFaitsMarquants());

        return kpiRepository.save(kpi);
    }

    public List<KpiMensuelProjet> getByProjet(Long projetId) {
        return kpiRepository.findByProjetIdOrderByMois(projetId);
    }

    public void delete(Long id) { kpiRepository.deleteById(id); }
}
