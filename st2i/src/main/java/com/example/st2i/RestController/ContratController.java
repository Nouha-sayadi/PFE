package com.example.st2i.RestController;


import com.example.st2i.DTO.ContratRequest;
import com.example.st2i.Entities.Contrat;
import com.example.st2i.Services.ContratService;
import com.example.st2i.Services.EcheanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contrats")
@CrossOrigin("*")
public class ContratController {
    @Autowired
    private ContratService contratService;

    @PostMapping
    public Contrat create(@RequestBody ContratRequest req) { return contratService.create(req); }

    @GetMapping("/projet/{id}")
    public List<Contrat> getByProjet(@PathVariable Long id) { return contratService.getByProjet(id); }

    @PutMapping("/{id}")
    public Contrat update(@PathVariable Long id, @RequestBody ContratRequest req) { return contratService.update(id, req); }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { contratService.delete(id); }
}
