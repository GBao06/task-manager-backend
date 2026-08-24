package com.taskmanager.task.dto;

import com.taskmanager.task.Quadrant;
import jakarta.validation.constraints.NotNull;

public class UpdateQuadrantRequest {

    @NotNull(message = "Phai chon vung (quadrant)")
    private Quadrant quadrant;

    public Quadrant getQuadrant() {
        return quadrant;
    }

    public void setQuadrant(Quadrant quadrant) {
        this.quadrant = quadrant;
    }
}
