package com.taskmanager.team.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateTeamRequest {

    @NotBlank(message = "Ten nhom khong duoc de trong")
    private String name;

    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}