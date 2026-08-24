package com.taskmanager.task.dto;

import com.taskmanager.task.TaskStatus;
import com.taskmanager.task.TaskType;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Cac truong deu khong bat buoc (nullable) - chi field nao duoc gui len
 * moi duoc cap nhat, giu nguyen cac field con lai (partial update).
 */
public class UpdateTaskRequest {

    private String title;
    private String description;
    private TaskType taskType;
    private LocalDate dueDate;
    private TaskStatus status;
    private UUID assigneeId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public UUID getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(UUID assigneeId) {
        this.assigneeId = assigneeId;
    }
}