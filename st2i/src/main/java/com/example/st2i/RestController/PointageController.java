package com.example.st2i.RestController;

import com.example.st2i.DTO.PointageRequest;
import com.example.st2i.DTO.PointageResponse;
import com.example.st2i.Entities.User;
import com.example.st2i.Services.PointageService;
import com.example.st2i.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pointages")
@CrossOrigin("*")
public class PointageController {

    @Autowired
    private PointageService pointageService;
    @Autowired private UserService userService;

    // ✅ Ressource connectée pointe son temps
    @PostMapping("/pointer")
    public PointageResponse pointer(
            JwtAuthenticationToken authentication,
            @RequestBody PointageRequest request) {
        String keycloakId = authentication.getToken().getSubject();
        User user = userService.findByKeycloakId(keycloakId);
        return pointageService.pointer(user.getId(), request);
    }

    // ✅ Pointer plusieurs mois
    @PostMapping("/pointer/batch")
    public List<PointageResponse> pointerBatch(
            JwtAuthenticationToken authentication,
            @RequestBody List<PointageRequest> requests) {
        String keycloakId = authentication.getToken().getSubject();
        User user = userService.findByKeycloakId(keycloakId);
        return pointageService.pointerBatch(user.getId(), requests);
    }

    // ✅ Mes pointages sur un projet
    @GetMapping("/mes-pointages/projet/{projetId}")
    public List<PointageResponse> getMesPointages(
            JwtAuthenticationToken authentication,
            @PathVariable Long projetId) {
        String keycloakId = authentication.getToken().getSubject();
        User user = userService.findByKeycloakId(keycloakId);
        return pointageService.getMesPointages(user.getId(), projetId);
    }

    // ✅ Tous mes pointages
    @GetMapping("/mes-pointages")
    public List<PointageResponse> getMesPointagesAll(
            JwtAuthenticationToken authentication) {
        String keycloakId = authentication.getToken().getSubject();
        User user = userService.findByKeycloakId(keycloakId);
        return pointageService.getMesPointagesAll(user.getId());
    }

    // ✅ Vue admin — tous les pointages d'un projet
    @GetMapping("/projet/{projetId}")
    public List<PointageResponse> getByProjet(@PathVariable Long projetId) {
        return pointageService.getByProjet(projetId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        pointageService.delete(id);
    }
}
