package com.example.st2i.Services;

import com.example.st2i.DTO.AffectationRequest;
import com.example.st2i.DTO.AffectationResponse;
import com.example.st2i.Entities.Affectation;
import com.example.st2i.Entities.Profil;
import com.example.st2i.Entities.Projet;
import com.example.st2i.Entities.User;
import com.example.st2i.Repositories.*;
import com.example.st2i.enums.NotificationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AffectationService {

    @Autowired private AffectationRepository affectationRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ProjetRepository projetRepository;
    @Autowired private ProfilRepository profilRepository;
    @Autowired private EstimationService estimationService;
    @Autowired private TCCRepository tccRepository;
    @Autowired private NotificationService notificationService;

    public AffectationResponse affecter(AffectationRequest request) {

        User user = userRepository.findById(request.getUtilisateurId())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        boolean aTcc = tccRepository
                .findTopByUtilisateurIdOrderByAnneeDesc(request.getUtilisateurId())
                .isPresent();
        if (!aTcc) {
            throw new RuntimeException(
                    "Ce membre n'a pas de TCC. Veuillez d'abord lui créer un TCC avant de l'affecter.");
        }

        Projet projet = projetRepository.findById(request.getProjetId())
                .orElseThrow(() -> new RuntimeException("Projet non trouvé"));

        Profil profil = profilRepository.findById(request.getProfilId())
                .orElseThrow(() -> new RuntimeException("Profil non trouvé"));

        boolean exists = affectationRepository
                .findByProjetId(request.getProjetId())
                .stream()
                .anyMatch(a -> a.getUtilisateur().getId().equals(request.getUtilisateurId()));

        if (exists) {
            throw new RuntimeException("Utilisateur déjà affecté à ce projet");
        }

        Affectation affectation = new Affectation();
        affectation.setUtilisateur(user);
        affectation.setProjet(projet);
        affectation.setProfil(profil);
        affectation.setDateAffectation(LocalDate.now());

        Affectation saved = affectationRepository.save(affectation);

        if (user.getKeycloakId() != null) {
            notificationService.createAndPush(
                    user.getKeycloakId(),
                    NotificationType.PROJECT_ASSIGNMENT,
                    "Nouvelle affectation",
                    "Vous avez été affecté au projet « " + projet.getTitre() + " ».",
                    projet.getId()
            );
        }

        return toDTO(saved);
    }

    public List<AffectationResponse> getMembresProjet(Long projetId) {
        return affectationRepository.findByProjetId(projetId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<AffectationResponse> getProjetsUser(Long userId) {
        return affectationRepository.findByUtilisateurId(userId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public void retirerUser(Long affectationId) {
        affectationRepository.deleteById(affectationId);
    }

    private AffectationResponse toDTO(Affectation a) {
        return new AffectationResponse(
                a.getId(),
                a.getDateAffectation() != null ? a.getDateAffectation().toString() : null,
                new AffectationResponse.UserSummary(
                        a.getUtilisateur().getId(),
                        a.getUtilisateur().getNom(),
                        a.getUtilisateur().getPrenom()
                ),
                new AffectationResponse.ProjetSummary(
                        a.getProjet().getId(),
                        a.getProjet().getTitre(),
                        a.getProjet().getStatut() != null ? a.getProjet().getStatut().name() : null,
                        a.getProjet().getTauxAvancement(),
                        a.getProjet().getBudgetInitial(),
                        a.getProjet().getDateDemarrage() != null ? a.getProjet().getDateDemarrage().toString() : null,
                        a.getProjet().getDateFinPrevu() != null ? a.getProjet().getDateFinPrevu().toString() : null
                ),
                new AffectationResponse.ProfilSummary(
                        a.getProfil().getId(),
                        a.getProfil().getCode(),
                        a.getProfil().getNom()
                )
        );
    }


}