package com.example.st2i.Services;

import com.example.st2i.Entities.User;
import com.example.st2i.Exception.DocumentNotFoundException;
import com.example.st2i.Exception.FileTooLargeException;
import com.example.st2i.Exception.InvalidFileTypeException;
import com.example.st2i.Repositories.UserRepository;
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
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/** Gère la photo de profil (unique par utilisateur) — stockage dédié, distinct du système Document polymorphique. */
@Service
public class UserPhotoService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    private static final long MAX_SIZE = 2L * 1024 * 1024; // 2 Mo
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");
    private static final Map<String, Set<String>> ALLOWED_MIME_BY_EXTENSION = Map.of(
            "jpg", Set.of("image/jpeg"),
            "jpeg", Set.of("image/jpeg"),
            "png", Set.of("image/png"),
            "webp", Set.of("image/webp")
    );

    private final UserRepository userRepository;

    public UserPhotoService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User upload(Long userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé (id=" + userId + ")."));

        if (file == null || file.isEmpty()) {
            throw new InvalidFileTypeException("Le fichier est vide.");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new FileTooLargeException("L'image dépasse la taille maximale autorisée (2 Mo).");
        }

        String originalName = sanitizeFileName(file.getOriginalFilename());
        String extension = extractExtension(originalName);

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new InvalidFileTypeException(
                    "Type d'image non autorisé (.%s). Types acceptés : JPG, PNG, WEBP.".formatted(extension));
        }
        Set<String> allowedMimes = ALLOWED_MIME_BY_EXTENSION.get(extension);
        if (allowedMimes == null || !allowedMimes.contains(file.getContentType())) {
            throw new InvalidFileTypeException(
                    "Le contenu du fichier ne correspond pas à un type d'image autorisé (JPG, PNG, WEBP).");
        }

        deleteExistingPhotoFile(user);

        String storedName = UUID.randomUUID() + "." + extension;
        try {
            Path targetDir = userPhotoDir(userId);
            Files.createDirectories(targetDir);
            Path targetFile = targetDir.resolve(storedName).normalize();
            if (!targetFile.startsWith(targetDir)) {
                throw new InvalidFileTypeException("Nom de fichier invalide.");
            }
            file.transferTo(targetFile);

            user.setPhotoUrl(relativePath(userId, storedName));
            return userRepository.save(user);
        } catch (IOException e) {
            throw new UncheckedIOException("Échec de l'enregistrement de la photo.", e);
        }
    }

    public Resource loadPhoto(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé (id=" + userId + ")."));

        if (user.getPhotoUrl() == null) {
            throw new DocumentNotFoundException("Aucune photo de profil pour cet utilisateur.");
        }

        try {
            Path filePath = uploadRoot().resolve(user.getPhotoUrl()).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new DocumentNotFoundException("Photo de profil introuvable sur le serveur.");
            }
            return resource;
        } catch (MalformedURLException e) {
            throw new DocumentNotFoundException("Photo de profil introuvable sur le serveur.");
        }
    }

    /** Type MIME réel de la photo actuelle, déduit de son extension de stockage. */
    public String resolvePhotoMimeType(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé (id=" + userId + ")."));
        if (user.getPhotoUrl() == null) {
            throw new DocumentNotFoundException("Aucune photo de profil pour cet utilisateur.");
        }
        String extension = extractExtension(user.getPhotoUrl());
        Set<String> mimes = ALLOWED_MIME_BY_EXTENSION.get(extension);
        return mimes != null ? mimes.iterator().next() : "application/octet-stream";
    }

    private void deleteExistingPhotoFile(User user) {
        if (user.getPhotoUrl() == null) return;
        try {
            Path existing = uploadRoot().resolve(user.getPhotoUrl()).normalize();
            Files.deleteIfExists(existing);
        } catch (IOException e) {
            throw new UncheckedIOException("Échec de la suppression de l'ancienne photo.", e);
        }
    }

    private Path uploadRoot() {
        return Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    private Path userPhotoDir(Long userId) {
        return uploadRoot().resolve("USER_PHOTOS").resolve(String.valueOf(userId)).normalize();
    }

    private String relativePath(Long userId, String storedName) {
        return Paths.get("USER_PHOTOS", String.valueOf(userId), storedName).toString();
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
