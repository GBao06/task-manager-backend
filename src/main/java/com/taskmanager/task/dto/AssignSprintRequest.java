package com.taskmanager.task.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class AssignSprintRequest {

    @NotNull(message = "Phai chon sprint")
    private UUID sprintId;

    public UUID getSprintId() {
        return sprintId;
    }

    public void setSprintId(UUID sprintId) {
        this.sprintId = sprintId;
    }
}