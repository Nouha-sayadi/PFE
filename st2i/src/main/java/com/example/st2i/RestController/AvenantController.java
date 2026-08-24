package com.example.st2i.RestController;

import com.example.st2i.DTO.AvenantRequest;
import com.example.st2i.DTO.AvenantResponse;
import com.example.st2i.Services.AvenantService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/avenants")
@CrossOrigin("*")
public class AvenantController {

    @Autowired private AvenantService avenantService;

    @PostMapping
    public AvenantResponse create(@Valid @RequestBody AvenantRequest req) {
        return avenantService.create(req);
    }

    @GetMapping("/projet/{projetId}")
    public List<AvenantResponse> getByProjet(@PathVariable Long projetId) {
        return avenantService.getByProjet(projetId);
    }

    @PutMapping("/{id}")
    public AvenantResponse update(@PathVariable Long id,
                                  @Valid @RequestBody AvenantRequest req) {
        return avenantService.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        avenantService.delete(id);
    }
}