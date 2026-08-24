package com.taskmanager.team.dto;

import java.util.UUID;

public class TeamResponse {

    private UUID id;
    private String name;
    private String description;
    private String ownerEmail;

    public TeamResponse(UUID id, String name, String description, String ownerEmail) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.ownerEmail = ownerEmail;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }
}