package com.taskmanager.team.dto;

import com.taskmanager.team.TeamRole;

import java.util.UUID;

public class TeamMemberResponse {

    private UUID id;
    private String fullName;
    private String email;
    private TeamRole roleInTeam;

    public TeamMemberResponse(UUID id, String fullName, String email, TeamRole roleInTeam) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.roleInTeam = roleInTeam;
    }

    public UUID getId() {
        return id;
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