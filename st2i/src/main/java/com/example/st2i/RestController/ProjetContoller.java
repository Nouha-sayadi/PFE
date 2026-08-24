package com.example.st2i.RestController;

import com.example.st2i.DTO.ProjetCompleteRequest;
import com.example.st2i.Entities.Projet;
import com.example.st2i.Services.ProjetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projets")
@CrossOrigin("*")
public class ProjetContoller {

    @Autowired
    private ProjetService projetService;


    @PostMapping
    public Projet create(@Valid @RequestBody Projet projet) {
        return projetService.save(projet);
    }

    @GetMapping
    public List<Projet> getAll() {
        return projetService.findAll();
    }
    @GetMapping("/{id}")
    public Projet getById(@PathVariable Long id) {
        return projetService.findById(id);
    }

    @PutMapping("/{id}")
    public Projet update(@PathVariable Long id, @Valid @RequestBody Projet projet) {
        return projetService.update(id, projet);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        projetService.delete(id);
    }

    @PatchMapping("/{id}/complete")
    public Projet complete(@PathVariable Long id, @RequestBody ProjetCompleteRequest req) {
        return projetService.completeEmptyFields(id, req);
    }

}
