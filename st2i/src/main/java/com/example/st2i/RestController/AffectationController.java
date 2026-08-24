package com.example.st2i.RestController;

import com.example.st2i.DTO.AffectationRequest;
import com.example.st2i.DTO.AffectationResponse;
import com.example.st2i.Entities.Affectation;
import com.example.st2i.Entities.User;
import com.example.st2i.Services.AffectationService;
import com.example.st2i.Services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/affectations")
@CrossOrigin("*")
public class AffectationController {

    @Autowired private AffectationService affectationService;
    @Autowired private UserService userService;



    @PostMapping
    public AffectationResponse affecter(@Valid @RequestBody AffectationRequest request) {
        return affectationService.affecter(request);
    }

    @GetMapping("/projet/{projetId}")
    public List<AffectationResponse> getMembres(@PathVariable Long projetId) {
        return affectationService.getMembresProjet(projetId);
    }

    @GetMapping("/user/{userId}")
    public List<AffectationResponse> getProjetsUser(@PathVariable Long userId) {
        return affectationService.getProjetsUser(userId);
    }

    @DeleteMapping("/{id}")
    public void retirer(@PathVariable Long id) {
        affectationService.retirerUser(id);
    }


    @GetMapping("/mes-projets")
    public List<AffectationResponse> getMesProjets(JwtAuthenticationToken authentication) {
        String keycloakId = authentication.getToken().getSubject();
        User user = userService.findByKeycloakId(keycloakId);
        return affectationService.getProjetsUser(user.getId());
    }

}