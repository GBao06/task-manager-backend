package com.taskmanager.gantt.dto;

import java.time.LocalDate;

/**
 * Cac truong deu khong bat buoc - chi field nao gui len moi duoc cap nhat
 * (giong co che partial update cua UpdateTaskRequest).
 */
public class UpdateScheduleRequest {

    private LocalDate startDate;
    private Integer durationDays;
    private Boolean isMilestone;

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public Integer getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(Integer durationDays) {
        this.durationDays = durationDays;
    }

    public Boolean getIsMilestone() {
        return isMilestone;
    }

    public void setIsMilestone(Boolean isMilestone) {
        this.isMilestone = isMilestone;
    }
}