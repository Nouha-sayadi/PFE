package com.example.st2i.Services;

import com.example.st2i.Entities.Document;
import com.example.st2i.Exception.DocumentNotFoundException;
import com.example.st2i.Exception.FileTooLargeException;
import com.example.st2i.Exception.InvalidFileTypeException;
import com.example.st2i.Repositories.ContratRepository;
import com.example.st2i.Repositories.DocumentRepository;
import com.example.st2i.Repositories.EcheanceFacturationRepository;
import com.example.st2i.Repositories.LivrableRepository;
import com.example.st2i.enums.TypeEntiteDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class DocumentService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Value("${app.upload.max-size}")
    private long maxSize;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("pdf", "jpg", "jpeg", "png", "docx");

    private static final Map<String, Set<String>> ALLOWED_MIME_BY_EXTENSION = Map.of(
            "pdf", Set.of("application/pdf"),
            "jpg", Set.of("image/jpeg"),
            "jpeg", Set.of("image/jpeg"),
            "png", Set.of("image/png"),
            "docx", Set.of("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
    );

    private final DocumentRepository documentRepository;
    private final ContratRepository contratRepository;
    private final LivrableRepository livrableRepository;
    private final EcheanceFacturationRepository echeanceFacturationRepository;

    public DocumentService(DocumentRepository documentRepository,
                            ContratRepository contratRepository,
                            LivrableRepository livrableRepository,
                            EcheanceFacturationRepository echeanceFacturationRepository) {
        this.documentRepository = documentRepository;
        this.contratRepository = contratRepository;
        this.livrableRepository = livrableRepository;
        this.echeanceFacturationRepository = echeanceFacturationRepository;
    }

    public Document upload(TypeEntiteDocument entityType, Long entityId, MultipartFile file) {
        checkEntityExists(entityType, entityId);

        if (file == null || file.isEmpty()) {
            throw new InvalidFileTypeException("Le fichier est vide.");
        }
        if (file.getSize() > maxSize) {
            throw new FileTooLargeException("Le fichier dépasse la taille maximale autorisée (10 Mo).");
        }

        String originalName = sanitizeFileName(file.getOriginalFilename());
        String extension = extractExtension(originalName);

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new InvalidFileTypeException(
                    "Type de fichier non autorisé (.%s). Types acceptés : PDF, JPG, PNG, DOCX.".formatted(extension));
        }
        Set<String> allowedMimes = ALLOWED_MIME_BY_EXTENSION.get(extension);
        if (allowedMimes == null || !allowedMimes.contains(file.getContentType())) {
            throw new InvalidFileTypeException(
                    "Le contenu du fichier ne correspond pas à un type autorisé (PDF, JPG, PNG, DOCX).");
        }

        String storedName = UUID.randomUUID() + "." + extension;

        try {
            Path targetDir = resolveEntityDir(entityType, entityId);
            Files.createDirectories(targetDir);
            Path targetFile = targetDir.resolve(storedName).normalize();
            if (!targetFile.startsWith(targetDir)) {
                throw new InvalidFileTypeException("Nom de fichier invalide.");
            }
            file.transferTo(targetFile);

            Document document = Document.builder()
                    .nomFichier(originalName)
                    .nomStocke(storedName)
                    .cheminFichier(relativePath(entityType, entityId, storedName))
                    .typeMime(file.getContentType())
                    .taille(file.getSize())
                    .dateUpload(LocalDateTime.now())
                    .entityType(entityType)
                    .entityId(entityId)
                    .build();
            return documentRepository.save(document);
        } catch (IOException e) {
            throw new UncheckedIOException("Échec de l'enregistrement du fichier.", e);
        }
    }

    /**
     * Enregistre un PDF généré automatiquement (ex: facture) comme Document.
     * Remplace le document généré précédent pour cette entité s'il existe — les uploads manuels ne sont jamais touchés.
     */
    public Document saveGeneratedPdf(TypeEntiteDocument entityType, Long entityId, byte[] pdfBytes, String fileName) {
        checkEntityExists(entityType, entityId);

        documentRepository.findFirstByEntityTypeAndEntityIdAndGenereTrueOrderByDateUploadDesc(entityType, entityId)
                .ifPresent(existing -> delete(existing.getId()));

        String storedName = UUID.randomUUID() + ".pdf";
        try {
            Path targetDir = resolveEntityDir(entityType, entityId);
            Files.createDirectories(targetDir);
            Path targetFile = targetDir.resolve(storedName).normalize();
            if (!targetFile.startsWith(targetDir)) {
                throw new InvalidFileTypeException("Nom de fichier invalide.");
            }
            Files.write(targetFile, pdfBytes);

            Document document = Document.builder()
                    .nomFichier(sanitizeFileName(fileName))
                    .nomStocke(storedName)
                    .cheminFichier(relativePath(entityType, entityId, storedName))
                    .typeMime("application/pdf")
                    .taille((long) pdfBytes.length)
                    .dateUpload(LocalDateTime.now())
                    .entityType(entityType)
                    .entityId(entityId)
                    .genere(true)
                    .build();
            return documentRepository.save(document);
        } catch (IOException e) {
            throw new UncheckedIOException("Échec de l'enregistrement du PDF généré.", e);
        }
    }

    public List<Document> getByEntity(TypeEntiteDocument entityType, Long entityId) {
        return documentRepository.findByEntityTypeAndEntityIdOrderByDateUploadDesc(entityType, entityId);
    }

    public Document getById(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new DocumentNotFoundException("Document non trouvé (id=" + id + ")."));
    }

    public Resource loadAsResource(Long id) {
        Document document = getById(id);
        try {
            Path filePath = uploadRoot().resolve(document.getCheminFichier()).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new DocumentNotFoundException("Fichier introuvable sur le serveur pour le document id=" + id + ".");
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new DocumentNotFoundException("Fichier introuvable sur le serveur pour le document id=" + id + ".");
        }
    }

    public void delete(Long id) {
        Document document = getById(id);
        try {
            Path filePath = uploadRoot().resolve(document.getCheminFichier()).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new UncheckedIOException("Échec de la suppression du fichier.", e);
        }
        documentRepository.delete(document);
    }

    private void checkEntityExists(TypeEntiteDocument entityType, Long entityId) {
        boolean exists = switch (entityType) {
            case CONTRAT -> contratRepository.existsById(entityId);
            case LIVRABLE -> livrableRepository.existsById(entityId);
            case ECHEANCE_FACTURATION -> echeanceFacturationRepository.existsById(entityId);
        };
        if (!exists) {
            throw new DocumentNotFoundException(
                    "Entité " + entityType + " (id=" + entityId + ") introuvable.");
        }
    }

    private Path uploadRoot() {
        return Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    private Path resolveEntityDir(TypeEntiteDocument entityType, Long entityId) {
        return uploadRoot().resolve(entityType.name()).resolve(String.valueOf(entityId)).normalize();
    }

    private String relativePath(TypeEntiteDocument entityType, Long entityId, String storedName) {
        return Paths.get(entityType.name(), String.valueOf(entityId), storedName).toString();
    }

    private String sanitizeFileName(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new InvalidFileTypeException("Nom de fichier manquant.");
        }
        String name = Paths.get(originalFilename).getFileName().toString();
        name = name.replaceAll("[^a-zA-Z0-9._-]", "_");
        if (name.contains("..")) {
            throw new InvalidFileTypeException("Nom de fichier invalide.");
        }
        return name;
    }

    private String extractExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        if (dot < 0 || dot == fileName.length() - 1) {
            throw new InvalidFileTypeException("Le fichier doit avoir une extension.");
        }
        return fileName.substring(dot + 1).toLowerCase();
    }
}
