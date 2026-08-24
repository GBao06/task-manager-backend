package com.taskmanager.notification;

import com.taskmanager.auth.User;
import com.taskmanager.notification.dto.NotificationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // UC12: Xem danh sach thong bao cua ban than
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getMyNotifications(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(notificationService.getMyNotifications(currentUser));
    }

    // Danh dau thong bao la da doc
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<NotificationResponse> markAsRead(@PathVariable UUID notificationId,
                                                             @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(notificationService.markAsRead(notificationId, currentUser));
    }
}