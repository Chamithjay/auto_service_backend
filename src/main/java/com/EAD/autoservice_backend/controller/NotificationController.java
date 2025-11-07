package com.EAD.autoservice_backend.controller;

import com.EAD.autoservice_backend.model.Notification;
import com.EAD.autoservice_backend.model.Role;
import com.EAD.autoservice_backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for notification management.
 * Handles notification creation, retrieval, and status updates.
 */
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Creates a new notification.
     *
     * @param notification the notification to create
     * @return ResponseEntity containing the created notification
     */
    @PostMapping
    public ResponseEntity<Notification> createNotification(@RequestBody Notification notification) {
        Notification created = notificationService.createNotification(notification);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * Retrieves all notifications.
     *
     * @return ResponseEntity containing list of all notifications
     */
    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    /**
     * Retrieves notifications for a specific role.
     *
     * @param role the role to filter notifications by
     * @return ResponseEntity containing list of notifications for the role
     */
    @GetMapping("/role/{role}")
    public ResponseEntity<List<Notification>> getNotificationsByRole(@PathVariable Role role) {
        return ResponseEntity.ok(notificationService.getNotificationsByRole(role));
    }

    /**
     * Retrieves unread notifications for a specific role.
     *
     * @param role the role to filter unread notifications by
     * @return ResponseEntity containing list of unread notifications for the role
     */
    @GetMapping("/role/{role}/unread")
    public ResponseEntity<List<Notification>> getUnreadNotificationsByRole(@PathVariable Role role) {
        return ResponseEntity.ok(notificationService.getUnreadNotificationsByRole(role));
    }

    /**
     * Retrieves notifications for a specific user.
     *
     * @param userId the user ID to filter notifications by
     * @return ResponseEntity containing list of notifications for the user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getNotificationsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getNotificationsByUserId(userId));
    }

    /**
     * Retrieves unread notifications for a specific user.
     *
     * @param userId the user ID to filter unread notifications by
     * @return ResponseEntity containing list of unread notifications for the user
     */
    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<Notification>> getUnreadNotificationsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getUnreadNotificationsByUserId(userId));
    }

    /**
     * Marks a notification as read.
     *
     * @param id the notification ID to mark as read
     * @return ResponseEntity containing the updated notification
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<Notification> markAsRead(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.markAsRead(id));
    }

    /**
     * Marks all notifications as read for a specific role.
     *
     * @param role the role whose notifications should be marked as read
     * @return ResponseEntity containing success message
     */
    @PutMapping("/role/{role}/read-all")
    public ResponseEntity<String> markAllAsReadByRole(@PathVariable Role role) {
        notificationService.markAllAsReadByRole(role);
        return ResponseEntity.ok("All notifications marked as read for role: " + role);
    }

    /**
     * Marks all notifications as read for a specific user.
     *
     * @param userId the user ID whose notifications should be marked as read
     * @return ResponseEntity containing success message
     */
    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<String> markAllAsReadByUserId(@PathVariable Long userId) {
        notificationService.markAllAsReadByUserId(userId);
        return ResponseEntity.ok("All notifications marked as read for user: " + userId);
    }

    /**
     * Deletes a notification.
     *
     * @param id the notification ID to delete
     * @return ResponseEntity containing success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok("Notification deleted successfully");
    }

    /**
     * Gets the count of unread notifications for a specific role.
     *
     * @param role the role to count unread notifications for
     * @return ResponseEntity containing the count of unread notifications
     */
    @GetMapping("/role/{role}/unread-count")
    public ResponseEntity<Long> getUnreadCountByRole(@PathVariable Role role) {
        return ResponseEntity.ok(notificationService.countUnreadByRole(role));
    }

    /**
     * Gets the count of unread notifications for a specific user.
     *
     * @param userId the user ID to count unread notifications for
     * @return ResponseEntity containing the count of unread notifications
     */
    @GetMapping("/user/{userId}/unread-count")
    public ResponseEntity<Long> getUnreadCountByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.countUnreadByUserId(userId));
    }
}
