package com.example.st2i.RestController;

import com.example.st2i.Services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin("*")
public class NotificationController {

    @Autowired private NotificationService notificationService;

    @GetMapping
    public List<Map<String, Object>> getAll(JwtAuthenticationToken auth) {
        return notificationService.getForUser(auth.getToken().getSubject());
    }

    @GetMapping("/unread-count")
    public Map<String, Long> getUnreadCount(JwtAuthenticationToken auth) {
        return Map.of("count", notificationService.getUnreadCount(auth.getToken().getSubject()));
    }

    @PutMapping("/{id}/read")
    public void markAsRead(@PathVariable Long id, JwtAuthenticationToken auth) {
        notificationService.markAsRead(id, auth.getToken().getSubject());
    }

    @PutMapping("/read-all")
    public void markAllAsRead(JwtAuthenticationToken auth) {
        notificationService.markAllAsRead(auth.getToken().getSubject());
    }
}
