package com.taskmanager.notification;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    List<Notification> findByUserIdOrderByCreatedAtDesc(UUID userId);

    // Dung cho scheduled job: kiem tra xem 1 user da duoc nhac ve 1 task
    // deadline cu the trong content chua, tranh gui trung thong bao nhieu lan/ngay
    boolean existsByUserIdAndContentContaining(UUID userId, String taskIdMarker);
}