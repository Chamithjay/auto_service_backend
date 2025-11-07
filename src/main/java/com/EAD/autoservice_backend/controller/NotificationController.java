package com.EAD.autoservice_backend.controller;

import com.EAD.autoservice_backend.model.Notification;
import com.EAD.autoservice_backend.model.Role;
import com.EAD.autoservice_backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Allow frontend to access
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<Notification> createNotification(@RequestBody Notification notification) {
        Notification created = notificationService.createNotification(notification);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<Notification>> getNotificationsByRole(@PathVariable Role role) {
        return ResponseEntity.ok(notificationService.getNotificationsByRole(role));
    }

    @GetMapping("/role/{role}/unread")
    public ResponseEntity<List<Notification>> getUnreadNotificationsByRole(@PathVariable Role role) {
        return ResponseEntity.ok(notificationService.getUnreadNotificationsByRole(role));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getNotificationsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getNotificationsByUserId(userId));
    }

    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<Notification>> getUnreadNotificationsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getUnreadNotificationsByUserId(userId));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Notification> markAsRead(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.markAsRead(id));
    }

    @PutMapping("/role/{role}/read-all")
    public ResponseEntity<String> markAllAsReadByRole(@PathVariable Role role) {
        notificationService.markAllAsReadByRole(role);
        return ResponseEntity.ok("All notifications marked as read for role: " + role);
    }

    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<String> markAllAsReadByUserId(@PathVariable Long userId) {
        notificationService.markAllAsReadByUserId(userId);
        return ResponseEntity.ok("All notifications marked as read for user: " + userId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok("Notification deleted successfully");
    }

    @GetMapping("/role/{role}/unread-count")
    public ResponseEntity<Long> getUnreadCountByRole(@PathVariable Role role) {
        return ResponseEntity.ok(notificationService.countUnreadByRole(role));
    }

    @GetMapping("/user/{userId}/unread-count")
    public ResponseEntity<Long> getUnreadCountByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.countUnreadByUserId(userId));
    }
}
