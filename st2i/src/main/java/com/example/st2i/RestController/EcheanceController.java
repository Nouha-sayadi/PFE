package com.example.st2i.RestController;

import com.example.st2i.DTO.EcheanceRequest;
import com.example.st2i.Entities.EcheanceFacturation;
import com.example.st2i.Services.DocumentService;
import com.example.st2i.Services.EcheanceService;
import com.example.st2i.Services.FactureGenerationService;
import com.example.st2i.enums.TypeEntiteDocument;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/echeances")
@CrossOrigin("*")
public class EcheanceController {
    @Autowired
    private EcheanceService echeanceService;

    @Autowired
    private FactureGenerationService factureGenerationService;

    @Autowired
    private DocumentService documentService;

    @PostMapping
    public EcheanceFacturation create(@Valid @RequestBody EcheanceRequest req) { return echeanceService.create(req); }
    @GetMapping("/projet/{id}") public List<EcheanceFacturation> getByProjet(@PathVariable Long id) { return echeanceService.getByProjet(id); }
    @PutMapping("/{id}") public EcheanceFacturation update(@PathVariable Long id, @Valid @RequestBody EcheanceRequest req) { return echeanceService.update(id, req); }
    @DeleteMapping("/{id}") public void delete(@PathVariable Long id) { echeanceService.delete(id); }

    @PostMapping("/{id}/generate-facture")
    public ResponseEntity<byte[]> generateFacture(@PathVariable Long id) {
        byte[] pdf = factureGenerationService.generate(id);
        String fileName = factureGenerationService.buildFileName(id);
        documentService.saveGeneratedPdf(TypeEntiteDocument.ECHEANCE_FACTURATION, id, pdf, fileName);

        String encodedName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedName)
                .body(pdf);
    }
}
