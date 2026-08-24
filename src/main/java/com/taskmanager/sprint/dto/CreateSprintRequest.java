package com.taskmanager.sprint.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public class CreateSprintRequest {

    @NotNull(message = "Phai chon nhom cho sprint")
    private UUID teamId;

    @NotBlank(message = "Ten sprint khong duoc de trong")
    private String name;

    @NotNull(message = "Phai co ngay bat dau")
    private LocalDate startDate;

    @NotNull(message = "Phai co ngay ket thuc")
    private LocalDate endDate;

    public UUID getTeamId() {
        return teamId;
    }

    public void setTeamId(UUID teamId) {
        this.teamId = teamId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}