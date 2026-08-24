package com.example.st2i.RestController;

import com.example.st2i.Entities.Bailleur;
import com.example.st2i.Repositories.BailleurRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/bailleurs")
@CrossOrigin("*")
public class BailleurController {

    @Autowired
    private BailleurRepository bailleurRepository;

    @GetMapping
    public List<Bailleur> getAll() {
        return bailleurRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bailleur> getById(@PathVariable Long id) {
        return bailleurRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Bailleur create(@Valid @RequestBody Bailleur bailleur) {
        return bailleurRepository.save(bailleur);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Bailleur> update(@PathVariable Long id, @Valid @RequestBody Bailleur bailleur) {
        if (!bailleurRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        bailleur.setId(id);
        return ResponseEntity.ok(bailleurRepository.save(bailleur));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!bailleurRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        bailleurRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}