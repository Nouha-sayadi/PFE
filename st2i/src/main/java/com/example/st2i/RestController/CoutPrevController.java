package com.example.st2i.RestController;

import com.example.st2i.DTO.CoutPrevRequest;
import com.example.st2i.DTO.CoutPrevResponse;
import com.example.st2i.Services.CoutPrevService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cout-prevs")
@CrossOrigin("*")
public class CoutPrevController {

    @Autowired private CoutPrevService coutPrevService;

    @PostMapping
    public CoutPrevResponse create(@RequestBody CoutPrevRequest request) {
        return coutPrevService.create(request);
    }
    @PostMapping("/batch")
    public List<CoutPrevResponse> createBatch(
            @RequestBody List<CoutPrevRequest> requests) {
        return requests.stream()
                .filter(r -> r.getChargePrevuM() != null && r.getChargePrevuM() > 0)
                .map(coutPrevService::create)
                .collect(Collectors.toList());
    }

    @GetMapping("/projet/{projetId}/user/{userId}")
    public List<CoutPrevResponse> getByProjetAndUser(
            @PathVariable Long projetId,
            @PathVariable Long userId) {
        return coutPrevService.getByProjetAndUser(projetId, userId);
    }

    @GetMapping("/projet/{projetId}")
    public List<CoutPrevResponse> getByProjet(@PathVariable Long projetId) {
        return coutPrevService.getByProjet(projetId);
    }

    @GetMapping("/projet/{projetId}/total")
    public Double getTotal(@PathVariable Long projetId) {
        return coutPrevService.getTotalCoutPrev(projetId);
    }

    @GetMapping("/projet/{projetId}/cumul")
    public Double getCumul(@PathVariable Long projetId) {
        return coutPrevService.getCumulRestant(projetId);
    }

    @GetMapping("/projet/{projetId}/plan-de-charge")
    public Map<String, Map<String, Object>> getPlanDeCharge(@PathVariable Long projetId) {
        return coutPrevService.getPlanDeCharge(projetId);
    }

    @GetMapping("/projet/{projetId}/total-par-mois")
    public Map<String, Double> getTotalParMois(@PathVariable Long projetId) {
        return coutPrevService.getTotalParMois(projetId);
    }

    @GetMapping("/projet/{projetId}/comparaison")
    public Map<String, Object> getComparaison(@PathVariable Long projetId) {
        return coutPrevService.getComparaison(projetId);
    }

    @PutMapping("/{id}")
    public CoutPrevResponse update(
            @PathVariable Long id,
            @RequestBody CoutPrevRequest request) {
        return coutPrevService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        coutPrevService.delete(id);
    }
}