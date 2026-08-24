package com.example.st2i.RestController;

import com.example.st2i.Entities.Document;
import com.example.st2i.Services.DocumentService;
import com.example.st2i.enums.TypeEntiteDocument;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin("*")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping("/{entityType}/{entityId}")
    public Document upload(@PathVariable TypeEntiteDocument entityType,
                            @PathVariable Long entityId,
                            @RequestParam("file") MultipartFile file) {
        return documentService.upload(entityType, entityId, file);
    }

    @GetMapping("/{entityType}/{entityId}")
    public List<Document> list(@PathVariable TypeEntiteDocument entityType,
                                @PathVariable Long entityId) {
        return documentService.getByEntity(entityType, entityId);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        Document document = documentService.getById(id);
        Resource resource = documentService.loadAsResource(id);
        String encodedName = URLEncoder.encode(document.getNomFichier(), StandardCharsets.UTF_8).replace("+", "%20");
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        document.getTypeMime() != null ? document.getTypeMime() : MediaType.APPLICATION_OCTET_STREAM_VALUE))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedName)
                .body(resource);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        documentService.delete(id);
    }
}
