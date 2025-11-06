package com.EAD.autoservice_backend.service;

import com.EAD.autoservice_backend.model.Notification;
import com.EAD.autoservice_backend.model.NotificationType;
import com.EAD.autoservice_backend.model.Role;
import com.EAD.autoservice_backend.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    @Transactional
    public Notification createNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    @Override
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    @Override
    public List<Notification> getNotificationsByRole(Role role) {
        return notificationRepository.findByTargetRole(role);
    }

    @Override
    public List<Notification> getUnreadNotificationsByRole(Role role) {
        return notificationRepository.findByTargetRoleAndIsReadFalse(role);
    }

    @Override
    public List<Notification> getNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserId(userId);
    }

    @Override
    public List<Notification> getUnreadNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalse(userId);
    }

    @Override
    @Transactional
    public Notification markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + notificationId));
        notification.setRead(true);
        return notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void markAllAsReadByRole(Role role) {
        List<Notification> notifications = notificationRepository.findByTargetRoleAndIsReadFalse(role);
        notifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(notifications);
    }

    @Override
    @Transactional
    public void markAllAsReadByUserId(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdAndIsReadFalse(userId);
        notifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(notifications);
    }

    @Override
    @Transactional
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    @Override
    public Long countUnreadByRole(Role role) {
        return notificationRepository.countByTargetRoleAndIsReadFalse(role);
    }

    @Override
    public Long countUnreadByUserId(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Override
    public Notification createNotificationForRole(String title, String message, Role role, NotificationType type) {
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setTargetRole(role);
        notification.setType(type);
        notification.setRead(false);
        return notificationRepository.save(notification);
    }

    @Override
    public Notification createNotificationForUser(String title, String message, Long userId, NotificationType type) {
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setUserId(userId);
//        notification.setTargetRole(Role.ALL); // Use ALL for user-specific notifications
        notification.setType(type);
        notification.setRead(false);
        return notificationRepository.save(notification);
    }
}
