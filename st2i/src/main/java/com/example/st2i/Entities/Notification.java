package com.example.st2i.Entities;

import com.example.st2i.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String recipientKeycloakId;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private String title;

    @Column(length = 512)
    private String message;

    @Column(name = "is_read")
    @Builder.Default
    private boolean read = false;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private Long projectId;
}
