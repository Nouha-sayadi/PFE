package com.example.st2i.RestController;

import com.example.st2i.DTO.LivrableRequest;
import com.example.st2i.DTO.LivrableResponse;
import com.example.st2i.Services.LivrableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/livrables")
@CrossOrigin("*")
public class LivrableController {

    @Autowired private LivrableService livrableService;

    @PostMapping
    public LivrableResponse create(@RequestBody LivrableRequest request) {
        return livrableService.create(request);
    }

    @GetMapping("/projet/{projetId}")
    public List<LivrableResponse> getByProjet(@PathVariable Long projetId) {
        return livrableService.getByProjet(projetId);
    }

    // ✅ Delivery global du projet
    @GetMapping("/projet/{projetId}/delivery")
    public Double getDelivery(@PathVariable Long projetId) {
        return livrableService.getDeliveryGlobal(projetId);
    }

    @PutMapping("/{id}")
    public LivrableResponse update(@PathVariable Long id,
                                   @RequestBody LivrableRequest request) {
        return livrableService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        livrableService.delete(id);
    }
}