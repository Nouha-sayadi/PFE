package com.example.st2i.RestController;

import com.example.st2i.Entities.Partenaire;
import com.example.st2i.Repositories.PartenaireRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/partenaires")
@CrossOrigin("*")
public class PartenaireController {

    @Autowired
    private PartenaireRepository partenaireRepository;

    @GetMapping
    public List<Partenaire> getAll() {
        return partenaireRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Partenaire> getById(@PathVariable Long id) {
        return partenaireRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Partenaire create(@Valid @RequestBody Partenaire partenaire) {
        return partenaireRepository.save(partenaire);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Partenaire> update(@PathVariable Long id, @Valid @RequestBody Partenaire partenaire) {
        if (!partenaireRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        partenaire.setId(id);
        return ResponseEntity.ok(partenaireRepository.save(partenaire));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!partenaireRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        partenaireRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}