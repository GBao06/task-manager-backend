package com.taskmanager.matrix;

import com.taskmanager.task.Quadrant;
import com.taskmanager.task.Task;
import com.taskmanager.task.TaskType;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Matrix Engine - UC08: Goi y tu dong phan loai cong viec.
 *
 * Nguyen tac tinh diem (giai doan 1 - chua co TaskDependency nen
 * importance chi dua theo taskType; khi lam xong module dependency
 * se cong them diem theo so luong task phu thuoc).
 */
@Service
public class MatrixEngineService {

    // Nguong de xac dinh "cao" hay "thap" - dung de chia 4 vung
    private static final float URGENCY_THRESHOLD = 5.0f;
    private static final float IMPORTANCE_THRESHOLD = 5.0f;

    /**
     * Tinh diem khan cap dua tren so ngay con lai den han (due_date).
     * Thang diem 0 - 10.
     */
    public float calculateUrgencyScore(LocalDate dueDate) {
        if (dueDate == null) {
            return 2.0f; // chua co han -> coi nhu khong gap
        }

        long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), dueDate);

        if (daysLeft < 0) return 10.0f;      // da qua han -> cuc ky khan cap
        if (daysLeft <= 2) return 9.0f;      // con <= 2 ngay
        if (daysLeft <= 7) return 6.0f;      // con trong tuan nay
        if (daysLeft <= 14) return 3.5f;     // con trong 2 tuan
        return 1.5f;                          // con nhieu thoi gian
    }

    /**
     * Tinh diem quan trong dua tren loai cong viec.
     * Thang diem 0 - 10.
     * (Sau nay co the cong them diem dua tren so luong task phu thuoc
     * o Gantt/CPM Module.)
     */
    public float calculateImportanceScore(TaskType taskType) {
        if (taskType == null) return 3.0f;

        return switch (taskType) {
            case BUG -> 8.5f;       // loi anh huong he thong dang chay
            case FEATURE -> 6.0f;   // tinh nang co gia tri nhung khong gap
            case REFACTOR -> 4.0f;  // gia tri dai han, khong anh huong ngay
            case OTHER -> 2.5f;
        };
    }

    /**
     * Xac dinh vung (quadrant) tu 2 diem so, dua tren nguong da dinh nghia.
     */
    public Quadrant determineQuadrant(float urgencyScore, float importanceScore) {
        boolean isUrgent = urgencyScore >= URGENCY_THRESHOLD;
        boolean isImportant = importanceScore >= IMPORTANCE_THRESHOLD;

        if (isUrgent && isImportant) return Quadrant.Q1;
        if (!isUrgent && isImportant) return Quadrant.Q2;
        if (isUrgent && !isImportant) return Quadrant.Q3;
        return Quadrant.Q4;
    }

    /**
     * Ham tien ich: tinh toan va gan truc tiep vao Task (dung khi tao/cap nhat task).
     */
    public void applySuggestion(Task task) {
        float urgency = calculateUrgencyScore(task.getDueDate());
        float importance = calculateImportanceScore(task.getTaskType());
        Quadrant suggested = determineQuadrant(urgency, importance);

        task.setUrgencyScore(urgency);
        task.setImportanceScore(importance);
        task.setQuadrant(suggested);
    }
}
