package com.example.st2i.Repositories;

import com.example.st2i.Entities.Notification;
import com.example.st2i.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByRecipientKeycloakIdOrderByCreatedAtDesc(String keycloakId, Pageable pageable);

    List<Notification> findByRecipientKeycloakIdOrderByCreatedAtDesc(String keycloakId);

    long countByRecipientKeycloakIdAndReadFalse(String keycloakId);

    boolean existsByRecipientKeycloakIdAndTypeAndProjectIdAndCreatedAtAfter(
            String keycloakId, NotificationType type, Long projectId, LocalDateTime since);
}
