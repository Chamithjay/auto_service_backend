package com.EAD.autoservice_backend.repository;

import com.EAD.autoservice_backend.model.Notification;
import com.EAD.autoservice_backend.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Get all notifications for a specific role
    List<Notification> findByTargetRole(Role targetRole);

    // Get unread notifications for a specific role
    List<Notification> findByTargetRoleAndIsReadFalse(Role targetRole);

    // Get notifications for a specific user
    List<Notification> findByUserId(Long userId);

    // Get unread notifications for a specific user
    List<Notification> findByUserIdAndIsReadFalse(Long userId);

    // Count unread notifications for a role
    Long countByTargetRoleAndIsReadFalse(Role targetRole);

    // Count unread notifications for a user
    Long countByUserIdAndIsReadFalse(Long userId);
}
