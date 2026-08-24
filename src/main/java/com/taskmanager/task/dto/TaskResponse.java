package com.taskmanager.task.dto;

import com.taskmanager.task.Quadrant;
import com.taskmanager.task.TaskStatus;
import com.taskmanager.task.TaskType;

import java.time.LocalDate;
import java.util.UUID;

public class TaskResponse {

    private UUID id;
    private UUID teamId;
    private UUID sprintId; // null = dang o Backlog
    private String title;
    private String description;
    private TaskType taskType;
    private LocalDate dueDate;
    private TaskStatus status;
    private String assigneeEmail;
    private Quadrant quadrant;
    private Float urgencyScore;
    private Float importanceScore;

    public TaskResponse(UUID id, UUID teamId, UUID sprintId, String title, String description, TaskType taskType,
                         LocalDate dueDate, TaskStatus status, String assigneeEmail,
                         Quadrant quadrant, Float urgencyScore, Float importanceScore) {
        this.id = id;
        this.teamId = teamId;
        this.sprintId = sprintId;
        this.title = title;
        this.description = description;
        this.taskType = taskType;
        this.dueDate = dueDate;
        this.status = status;
        this.assigneeEmail = assigneeEmail;
        this.quadrant = quadrant;
        this.urgencyScore = urgencyScore;
        this.importanceScore = importanceScore;
    }

    public UUID getId() {
        return id;
    }

    public UUID getTeamId() {
        return teamId;
    }

    public UUID getSprintId() {
        return sprintId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public String getAssigneeEmail() {
        return assigneeEmail;
    }

    public Quadrant getQuadrant() {
        return quadrant;
    }

    public Float getUrgencyScore() {
        return urgencyScore;
    }

    public Float getImportanceScore() {
        return importanceScore;
    }
}