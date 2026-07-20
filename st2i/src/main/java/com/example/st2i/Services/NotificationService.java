package com.example.st2i.Services;

import com.example.st2i.Entities.Notification;
import com.example.st2i.Repositories.NotificationRepository;
import com.example.st2i.enums.NotificationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class NotificationService {

    @Autowired private NotificationRepository notificationRepository;
    @Autowired private SimpMessagingTemplate messagingTemplate;

    public Notification createAndPush(String recipientKeycloakId,
                                      NotificationType type,
                                      String title,
                                      String message,
                                      Long projectId) {
        Notification notif = Notification.builder()
                .recipientKeycloakId(recipientKeycloakId)
                .type(type)
                .title(title)
                .message(message)
                .projectId(projectId)
                .build();

        Notification saved = notificationRepository.save(notif);

        // push temps réel au destinataire connecté
        try {
            messagingTemplate.convertAndSendToUser(
                    recipientKeycloakId,
                    "/queue/notifications",
                    toPayload(saved)
            );
        } catch (Exception e) {
            org.slf4j.LoggerFactory.getLogger(NotificationService.class)
                    .error("[WS] Echec push vers {} : {}", recipientKeycloakId, e.getMessage(), e);
        }

        return saved;
    }

    public List<Map<String, Object>> getForUser(String keycloakId) {
        return notificationRepository
                .findByRecipientKeycloakIdOrderByCreatedAtDesc(keycloakId)
                .stream()
                .limit(50)
                .map(this::toPayload)
                .toList();
    }

    public long getUnreadCount(String keycloakId) {
        return notificationRepository.countByRecipientKeycloakIdAndReadFalse(keycloakId);
    }

    public void markAsRead(Long id, String keycloakId) {
        notificationRepository.findById(id).ifPresent(n -> {
            if (n.getRecipientKeycloakId().equals(keycloakId)) {
                n.setRead(true);
                notificationRepository.save(n);
            }
        });
    }

    public void markAllAsRead(String keycloakId) {
        var list = notificationRepository.findByRecipientKeycloakIdOrderByCreatedAtDesc(keycloakId);
        list.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(list);
    }

    public boolean existsToday(String keycloakId, NotificationType type, Long projectId) {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        return notificationRepository.existsByRecipientKeycloakIdAndTypeAndProjectIdAndCreatedAtAfter(
                keycloakId, type, projectId, startOfDay);
    }

    private Map<String, Object> toPayload(Notification n) {
        return Map.of(
                "id",        n.getId(),
                "type",      n.getType().name(),
                "title",     n.getTitle(),
                "message",   n.getMessage(),
                "read",      n.isRead(),
                "createdAt", n.getCreatedAt().toString(),
                "projectId", n.getProjectId() != null ? n.getProjectId() : 0
        );
    }
}
