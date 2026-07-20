package com.example.st2i.Services;

import com.example.st2i.Entities.Affectation;
import com.example.st2i.Entities.CoutPrev;
import com.example.st2i.Repositories.AffectationRepository;
import com.example.st2i.Repositories.CoutPrevRepository;
import com.example.st2i.Repositories.PointageRepository;
import com.example.st2i.enums.NotificationType;
import com.example.st2i.enums.StatutProjet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class NotificationSchedulerService {

    @Autowired private AffectationRepository    affectationRepository;
    @Autowired private CoutPrevRepository       coutPrevRepository;
    @Autowired private PointageRepository       pointageRepository;
    @Autowired private NotificationService      notificationService;

    // Tous les jours à 8h00
    @Scheduled(cron = "0 0 8 * * *")
    public void envoyerRappelsPointage() {
        LocalDate today    = LocalDate.now();
        LocalDate lastMonth = today.minusMonths(1).withDayOfMonth(1);

        List<Affectation> affectations = affectationRepository.findAll().stream()
                .filter(a -> a.getProjet().getStatut() == StatutProjet.EN_COURS)
                .toList();

        for (Affectation aff : affectations) {
            if (aff.getUtilisateur().getKeycloakId() == null) continue;

            Long userId    = aff.getUtilisateur().getId();
            Long projectId = aff.getProjet().getId();
            String keycloakId = aff.getUtilisateur().getKeycloakId();

            // Chercher les mois planifiés (CoutPrev) dont la date est passée
            List<CoutPrev> coutsPrevs = coutPrevRepository.findByProjetIdAndRessourceId(projectId, userId);

            for (CoutPrev cp : coutsPrevs) {
                LocalDate moisPlanifie = cp.getMois();
                if (moisPlanifie == null || !moisPlanifie.isBefore(today)) continue;

                // Vérifier si le pointage de ce mois existe
                boolean pointageExiste = pointageRepository
                        .findByProjetIdAndRessourceIdAndMois(projectId, userId, moisPlanifie)
                        .isPresent();

                if (!pointageExiste) {
                    // Anti-doublon : une seule notif par jour par user/projet/type
                    boolean dejaEnvoyee = notificationService.existsToday(
                            keycloakId, NotificationType.POINTAGE_REMINDER, projectId);

                    if (!dejaEnvoyee) {
                        String moisLabel = moisPlanifie.getMonth().getDisplayName(
                                java.time.format.TextStyle.FULL, java.util.Locale.FRENCH)
                                + " " + moisPlanifie.getYear();

                        notificationService.createAndPush(
                                keycloakId,
                                NotificationType.POINTAGE_REMINDER,
                                "Pointage manquant",
                                "Votre pointage pour « " + aff.getProjet().getTitre()
                                        + " » (" + moisLabel + ") n'a pas encore été saisi.",
                                projectId
                        );
                        break; // une seule notif par projet par jour
                    }
                }
            }
        }
    }
}
