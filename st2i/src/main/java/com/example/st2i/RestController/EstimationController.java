package com.example.st2i.RestController;

import com.example.st2i.DTO.EstimationRequest;
import com.example.st2i.DTO.EstimationResponse;
import com.example.st2i.Services.EstimationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estimations")
@CrossOrigin("*")
public class EstimationController {

    @Autowired
    private EstimationService estimationService;

    @PostMapping
    public EstimationResponse create(@Valid @RequestBody EstimationRequest request) {
        return estimationService.create(request);
    }

    @GetMapping("/projet/{projetId}")
    public List<EstimationResponse> getByProjet(@PathVariable Long projetId) {
        return estimationService.getByProjet(projetId);
    }

    @GetMapping("/projet/{projetId}/total")
    public Double getTotalByProjet(@PathVariable Long projetId) {
        return estimationService.getTotalCoutEstime(projetId);
    }

    @PutMapping("/{id}")
    public EstimationResponse update(@PathVariable Long id, @Valid @RequestBody EstimationRequest request) {
        return estimationService.update(id, request);
    }

    @PutMapping("/{id}/recalculer")
    public EstimationResponse recalculer(@PathVariable Long id) {
        return estimationService.recalculer(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        estimationService.delete(id);
    }
}