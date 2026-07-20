package com.example.st2i.Services;

import com.example.st2i.DTO.EcheanceRequest;
import com.example.st2i.DTO.KpiMensuelRequest;
import com.example.st2i.Entities.EcheanceFacturation;
import com.example.st2i.Entities.KpiMensuelProjet;
import com.example.st2i.Entities.Projet;
import com.example.st2i.Repositories.ContratRepository;
import com.example.st2i.Repositories.EcheanceFacturationRepository;
import com.example.st2i.Repositories.KpiMensuelProjetRepository;
import com.example.st2i.Repositories.ProjetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

@Service
public class EcheanceService {

    @Autowired private EcheanceFacturationRepository echeanceRepository;
    @Autowired private ProjetRepository projetRepository;
    @Autowired private ContratRepository contratRepository;
    @Autowired private KpiMensuelProjetRepository kpiMensuelRepository;
    @Autowired private KpiAutoCalculeService kpiAutoCalculeService;
    @Autowired private KpiMensuelService kpiMensuelService;

    private static final DateTimeFormatter MOIS_FMT = DateTimeFormatter.ofPattern("yyyy-MM");

    public EcheanceFacturation create(EcheanceRequest req) {
        Projet projet = projetRepository.findById(req.getProjetId())
                .orElseThrow(() -> new RuntimeException("Projet non trouvé"));

        EcheanceFacturation e = EcheanceFacturation.builder()
                .projet(projet)
                .numero(req.getNumero())
                .objet(req.getObjet())
                .pourcentage(req.getPourcentage())
                .montantPrevu(req.getMontantPrevu())
                .montantFacture(req.getMontantFacture())
                .dateInitiale(req.getDateInitiale())
                .datePrevActualisee(req.getDatePrevActualisee())
                .dateReelle(req.getDateReelle())
                .emise(req.getEmise() != null ? req.getEmise() : false)
                .commentaire(req.getCommentaire())
                .build();

        if (req.getContratId() != null) {
            contratRepository.findById(req.getContratId())
                    .ifPresent(e::setContrat);
        }
        return echeanceRepository.save(e);
    }

    public List<EcheanceFacturation> getByProjet(Long projetId) {
        return echeanceRepository.findByProjetIdOrderByNumero(projetId);
    }

    public EcheanceFacturation update(Long id, EcheanceRequest req) {
        EcheanceFacturation e = echeanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Échéance non trouvée"));

        boolean wasEmise      = Boolean.TRUE.equals(e.getEmise());
        boolean willBeEmise   = Boolean.TRUE.equals(req.getEmise());
        boolean emiseChanged  = wasEmise != willBeEmise;
        boolean montantChange = !Objects.equals(e.getMontantFacture(), req.getMontantFacture());
        boolean dateChange    = !Objects.equals(e.getDateReelle(), req.getDateReelle());

        e.setNumero(req.getNumero());
        e.setObjet(req.getObjet());
        e.setPourcentage(req.getPourcentage());
        e.setMontantPrevu(req.getMontantPrevu());
        e.setMontantFacture(req.getMontantFacture());
        e.setDateInitiale(req.getDateInitiale());
        e.setDatePrevActualisee(req.getDatePrevActualisee());
        e.setDateReelle(req.getDateReelle());
        e.setEmise(req.getEmise() != null ? req.getEmise() : false);
        e.setCommentaire(req.getCommentaire());
        EcheanceFacturation saved = echeanceRepository.save(e);

        // Recalculer les KPI mensuels existants si emise, montantFacture ou dateReelle a changé
        if (emiseChanged || (willBeEmise && (montantChange || dateChange))) {
            Long projetId = req.getProjetId();
            if (projetId != null) {
                recalculerKpisExistants(projetId);
            }
        }

        return saved;
    }

    private void recalculerKpisExistants(Long projetId) {
        List<KpiMensuelProjet> kpis = kpiMensuelRepository.findByProjetIdOrderByMois(projetId);
        for (KpiMensuelProjet kpi : kpis) {
            String moisStr = kpi.getMois().format(MOIS_FMT);
            Double ev = kpi.getEarnedValue();
            KpiMensuelRequest recalcule = (ev != null && ev > 0)
                    ? kpiAutoCalculeService.recalculerAvecEV(projetId, moisStr, ev)
                    : kpiAutoCalculeService.calculerKpiPourMois(projetId, moisStr);
            recalcule.setFaitsMarquants(kpi.getFaitsMarquants());
            kpiMensuelService.saveOrUpdate(recalcule);
        }
    }

    public void delete(Long id) { echeanceRepository.deleteById(id); }


    public long calculerDerive(EcheanceFacturation e) {
        if (e.getDateInitiale() == null || e.getDateReelle() == null) return 0;
        return ChronoUnit.DAYS.between(e.getDateInitiale(), e.getDateReelle());
    }
}
