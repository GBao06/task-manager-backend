package com.taskmanager.sprint.dto;

import com.taskmanager.sprint.SprintStatus;

import java.time.LocalDate;
import java.util.UUID;

public class SprintResponse {

    private UUID id;
    private UUID teamId;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private SprintStatus status;

    public SprintResponse(UUID id, UUID teamId, String name, LocalDate startDate,
                           LocalDate endDate, SprintStatus status) {
        this.id = id;
        this.teamId = teamId;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public UUID getTeamId() {
        return teamId;
    }

    public String getName() {
        return name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public SprintStatus getStatus() {
        return status;
    }
}