package com.taskmanager.gantt.dto;

import com.taskmanager.task.Quadrant;
import com.taskmanager.task.TaskStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class GanttTaskResponse {

    private UUID id;
    private String title;
    private LocalDate startDate;
    private LocalDate dueDate;
    private Integer durationDays;
    private TaskStatus status;
    private Quadrant quadrant;
    private boolean isMilestone;
    private boolean isCritical;
    private List<UUID> dependsOnTaskIds; // danh sach cac task phai xong truoc task nay

    public GanttTaskResponse(UUID id, String title, LocalDate startDate, LocalDate dueDate,
                              Integer durationDays, TaskStatus status, Quadrant quadrant,
                              boolean isMilestone, boolean isCritical, List<UUID> dependsOnTaskIds) {
        this.id = id;
        this.title = title;
        this.startDate = startDate;
        this.dueDate = dueDate;
        this.durationDays = durationDays;
        this.status = status;
        this.quadrant = quadrant;
        this.isMilestone = isMilestone;
        this.isCritical = isCritical;
        this.dependsOnTaskIds = dependsOnTaskIds;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public Integer getDurationDays() {
        return durationDays;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public Quadrant getQuadrant() {
        return quadrant;
    }

    public boolean isMilestone() {
        return isMilestone;
    }

    public boolean isCritical() {
        return isCritical;
    }

    public List<UUID> getDependsOnTaskIds() {
        return dependsOnTaskIds;
    }
}