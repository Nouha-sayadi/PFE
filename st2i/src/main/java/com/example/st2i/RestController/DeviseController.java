package com.example.st2i.RestController;

import com.example.st2i.Entities.Devise;
import com.example.st2i.Repositories.DeviseRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/devises")
@CrossOrigin("*")
public class DeviseController {

    @Autowired
    private DeviseRepository deviseRepository;

    @GetMapping
    public List<Devise> getAll() {
        return deviseRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Devise> getById(@PathVariable Long id) {
        return deviseRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Devise create(@Valid @RequestBody Devise devise) {
        return deviseRepository.save(devise);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Devise> update(@PathVariable Long id, @Valid @RequestBody Devise devise) {
        if (!deviseRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        devise.setId(id);
        return ResponseEntity.ok(deviseRepository.save(devise));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!deviseRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        deviseRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}