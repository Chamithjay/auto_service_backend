package com.EAD.autoservice_backend.service;

import com.EAD.autoservice_backend.model.Notification;
import com.EAD.autoservice_backend.model.NotificationType;
import com.EAD.autoservice_backend.model.Role;

import java.util.List;

public interface NotificationService {

    // Basic CRUD
    Notification createNotification(Notification notification);
    List<Notification> getAllNotifications();
    void deleteNotification(Long notificationId);

    // By role
    List<Notification> getNotificationsByRole(Role role);
    List<Notification> getUnreadNotificationsByRole(Role role);
    void markAllAsReadByRole(Role role);
    Long countUnreadByRole(Role role);

    // By user
    List<Notification> getNotificationsByUserId(Long userId);
    List<Notification> getUnreadNotificationsByUserId(Long userId);
    void markAllAsReadByUserId(Long userId);
    Long countUnreadByUserId(Long userId);

    // Individual notifications
    Notification markAsRead(Long notificationId);

    // Event-based helper methods
    Notification createNotificationForRole(String title, String message, Role role, NotificationType type);
    Notification createNotificationForUser(String title, String message, Long userId, NotificationType type);
}
