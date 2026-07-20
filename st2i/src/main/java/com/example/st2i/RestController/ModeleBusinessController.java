package com.example.st2i.RestController;

import com.example.st2i.Entities.ModeleBusiness;
import com.example.st2i.Repositories.ModeleBusinessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/modeles")
@CrossOrigin("*")
public class ModeleBusinessController {

    @Autowired
    private ModeleBusinessRepository modeleBusinessRepository;

    @GetMapping
    public List<ModeleBusiness> getAll() {
        return modeleBusinessRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ModeleBusiness> getById(@PathVariable Long id) {
        return modeleBusinessRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ModeleBusiness create(@RequestBody ModeleBusiness modele) {
        return modeleBusinessRepository.save(modele);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ModeleBusiness> update(@PathVariable Long id, @RequestBody ModeleBusiness modele) {
        if (!modeleBusinessRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        modele.setId(id);
        return ResponseEntity.ok(modeleBusinessRepository.save(modele));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!modeleBusinessRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        modeleBusinessRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}