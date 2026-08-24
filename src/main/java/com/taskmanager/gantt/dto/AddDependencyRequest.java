package com.taskmanager.gantt.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class AddDependencyRequest {

    @NotNull(message = "Phai chon cong viec phai hoan thanh truoc")
    private UUID predecessorTaskId;

    public UUID getPredecessorTaskId() {
        return predecessorTaskId;
    }

    public void setPredecessorTaskId(UUID predecessorTaskId) {
        this.predecessorTaskId = predecessorTaskId;
    }
}