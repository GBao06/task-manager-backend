package com.taskmanager.team.dto;

import com.taskmanager.team.TeamRole;

import java.util.UUID;

public class TeamMemberResponse {

    private UUID id;
    private UUID userId;
    private String fullName;
    private String email;
    private TeamRole roleInTeam;

    public TeamMemberResponse(UUID id, UUID userId, String fullName, String email, TeamRole roleInTeam) {
        this.id = id;
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.roleInTeam = roleInTeam;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public TeamRole getRoleInTeam() {
        return roleInTeam;
    }
}