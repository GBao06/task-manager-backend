package com.taskmanager.sprint.dto;

import com.taskmanager.sprint.SprintStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateSprintStatusRequest {

    @NotNull(message = "Phai chon trang thai")
    private SprintStatus status;

    public SprintStatus getStatus() {
        return status;
    }

    public void setStatus(SprintStatus status) {
        this.status = status;
    }
}