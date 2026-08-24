package com.example.st2i.RestController;


import com.example.st2i.DTO.ContratExtractionResult;
import com.example.st2i.DTO.ContratRequest;
import com.example.st2i.Entities.Contrat;
import com.example.st2i.Services.ContratExtractionService;
import com.example.st2i.Services.ContratService;
import com.example.st2i.Services.EcheanceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/contrats")
@CrossOrigin("*")
public class ContratController {
    @Autowired
    private ContratService contratService;

    @Autowired
    private ContratExtractionService contratExtractionService;

    @PostMapping
    public Contrat create(@Valid @RequestBody ContratRequest req) { return contratService.create(req); }

    @PostMapping("/extract")
    public ContratExtractionResult extract(@RequestParam("file") MultipartFile file,
                                            @RequestParam(value = "projetId", required = false) Long projetId) {
        return contratExtractionService.extract(file, projetId);
    }

    @GetMapping("/projet/{id}")
    public List<Contrat> getByProjet(@PathVariable Long id) { return contratService.getByProjet(id); }

    @PutMapping("/{id}")
    public Contrat update(@PathVariable Long id, @Valid @RequestBody ContratRequest req) { return contratService.update(id, req); }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { contratService.delete(id); }
}
