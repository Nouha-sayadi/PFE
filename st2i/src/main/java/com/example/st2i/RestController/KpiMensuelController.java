package com.example.st2i.RestController;

import com.example.st2i.DTO.KpiMensuelRequest;
import com.example.st2i.Entities.KpiMensuelProjet;
import com.example.st2i.Services.KpiAutoCalculeService;
import com.example.st2i.Services.KpiMensuelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kpis")
@CrossOrigin("*")
public class KpiMensuelController {
    @Autowired
    private KpiMensuelService kpiService;

    @Autowired
    private KpiAutoCalculeService kpiAutoCalculeService;

    @PostMapping
    public KpiMensuelProjet save(@RequestBody KpiMensuelRequest req) { return kpiService.saveOrUpdate(req); }


    @GetMapping("/projet/{id}")
    public List<KpiMensuelProjet> getByProjet(@PathVariable Long id) { return kpiService.getByProjet(id); }


    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { kpiService.delete(id); }


    @GetMapping("/projet/{projetId}/calculer/{mois}")
    public KpiMensuelRequest calculerKpi(
            @PathVariable Long projetId,
            @PathVariable String mois) {
        return kpiAutoCalculeService.calculerKpiPourMois(projetId, mois);
    }

    //  Recalculer avec EV saisi par le chef de projet
    @GetMapping("/projet/{projetId}/calculer/{mois}/ev/{earnedValue}")
    public KpiMensuelRequest calculerAvecEV(
            @PathVariable Long projetId,
            @PathVariable String mois,
            @PathVariable Double earnedValue) {
        return kpiAutoCalculeService.recalculerAvecEV(projetId, mois, earnedValue);
    }
}
