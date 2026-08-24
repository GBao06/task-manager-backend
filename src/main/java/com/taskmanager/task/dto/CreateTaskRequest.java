package com.taskmanager.task.dto;

import com.taskmanager.task.TaskType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public class CreateTaskRequest {

    @NotNull(message = "Phai chon nhom cho cong viec")
    private UUID teamId;

    @NotBlank(message = "Tieu de khong duoc de trong")
    private String title;

    private String description;

    @NotNull(message = "Phai chon loai cong viec")
    private TaskType taskType;

    private LocalDate dueDate;

    // Khong bat buoc - co the chua phan cong nguoi phu trach luc tao
    private UUID assigneeId;

    public UUID getTeamId() {
        return teamId;
    }

    public void setTeamId(UUID teamId) {
        this.teamId = teamId;
    }

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

    public UUID getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(UUID assigneeId) {
        this.assigneeId = assigneeId;
    }
}