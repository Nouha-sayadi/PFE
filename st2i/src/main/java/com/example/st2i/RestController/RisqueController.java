package com.example.st2i.RestController;

import com.example.st2i.DTO.RisqueRequest;
import com.example.st2i.Entities.Risque;
import com.example.st2i.Services.RisqueService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/risques")
@CrossOrigin("*")
public class RisqueController {

    @Autowired
    private RisqueService risqueService;

    @PostMapping
    public Risque create(@Valid @RequestBody RisqueRequest req) {
        return risqueService.create(req);
    }

    @GetMapping("/projet/{id}")
    public List<Risque> getByProjet(@PathVariable Long id) {
        return risqueService.getByProjet(id);
    }

    @PutMapping("/{id}")
    public Risque update(@PathVariable Long id,
                         @Valid @RequestBody RisqueRequest req) {
        return risqueService.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        risqueService.delete(id);
    }
}