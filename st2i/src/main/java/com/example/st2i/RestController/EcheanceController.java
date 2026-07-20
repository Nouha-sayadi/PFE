package com.example.st2i.RestController;

import com.example.st2i.DTO.EcheanceRequest;
import com.example.st2i.Entities.EcheanceFacturation;
import com.example.st2i.Services.EcheanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/echeances")
@CrossOrigin("*")
public class EcheanceController {
    @Autowired
    private EcheanceService echeanceService;

    @PostMapping
    public EcheanceFacturation create(@RequestBody EcheanceRequest req) { return echeanceService.create(req); }
    @GetMapping("/projet/{id}") public List<EcheanceFacturation> getByProjet(@PathVariable Long id) { return echeanceService.getByProjet(id); }
    @PutMapping("/{id}") public EcheanceFacturation update(@PathVariable Long id, @RequestBody EcheanceRequest req) { return echeanceService.update(id, req); }
    @DeleteMapping("/{id}") public void delete(@PathVariable Long id) { echeanceService.delete(id); }
}
