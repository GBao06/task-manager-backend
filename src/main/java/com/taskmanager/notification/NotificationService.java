package com.taskmanager.notification;

import com.taskmanager.auth.User;
import com.taskmanager.notification.dto.NotificationResponse;
import com.taskmanager.task.Task;
import com.taskmanager.task.TaskRepository;
import com.taskmanager.task.TaskStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private TaskRepository taskRepository;

    /**
     * UC12 - Xem danh sach thong bao cua ban than (moi nhat truoc).
     */
    public List<NotificationResponse> getMyNotifications(User currentUser) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Danh dau 1 thong bao la da doc.
     */
    public NotificationResponse markAsRead(UUID notificationId, User currentUser) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay thong bao"));

        if (!notification.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Ban khong co quyen voi thong bao nay");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
        return toResponse(notification);
    }

    /**
     * Tao thong bao khi mot cong viec duoc phan cong cho ai do.
     * Duoc goi truc tiep tu TaskService khi assignee thay doi.
     */
    public void notifyAssignment(User assignee, Task task) {
        String content = "Ban duoc phan cong cong viec: " + task.getTitle();
        Notification notification = new Notification(assignee, content, NotificationType.ASSIGNMENT);
        notificationRepository.save(notification);
    }

    /**
     * UC12 - Tu dong quet va gui thong bao cho cong viec sap den han/qua han.
     * Chay 1 lan moi ngay luc 7h sang (co the doi lich chay o day).
     */
    @Scheduled(cron = "0 0 7 * * *")
    public void checkDeadlinesAndNotify() {
        List<Task> allTasks = taskRepository.findAll();
        LocalDate today = LocalDate.now();

        for (Task task : allTasks) {
            if (task.getDueDate() == null) continue;
            if (task.getStatus() == TaskStatus.DONE) continue;
            if (task.getAssignee() == null) continue;

            long daysLeft = ChronoUnit.DAYS.between(today, task.getDueDate());

            // Chi nhac khi con <= 2 ngay hoac da qua han
            if (daysLeft > 2) continue;

            // Danh dau bang id cua task trong noi dung de tranh gui trung
            // thong bao nhieu lan trong cung 1 ngay
            String taskMarker = "[task:" + task.getId() + "][date:" + today + "]";

            boolean alreadyNotifiedToday = notificationRepository
                    .existsByUserIdAndContentContaining(task.getAssignee().getId(), taskMarker);

            if (alreadyNotifiedToday) continue;

            String message = daysLeft < 0
                    ? "Cong viec \"" + task.getTitle() + "\" da qua han " + taskMarker
                    : "Cong viec \"" + task.getTitle() + "\" sap den han (con " + daysLeft + " ngay) " + taskMarker;

            Notification notification = new Notification(task.getAssignee(), message, NotificationType.DEADLINE);
            notificationRepository.save(notification);
        }
    }

    private NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getContent(),
                notification.getType(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }
}