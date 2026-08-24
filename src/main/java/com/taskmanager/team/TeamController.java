package com.taskmanager.team;

import com.taskmanager.auth.User;
import com.taskmanager.team.dto.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    @Autowired
    private TeamService teamService;

    // UC03: Tao nhom lam viec
    @PostMapping
    public ResponseEntity<TeamResponse> createTeam(@Valid @RequestBody CreateTeamRequest request,
                                                     @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(teamService.createTeam(request, currentUser));
    }

    // UC04: Moi thanh vien vao nhom
    @PostMapping("/{teamId}/members")
    public ResponseEntity<TeamMemberResponse> inviteMember(@PathVariable UUID teamId,
                                                             @Valid @RequestBody InviteMemberRequest request,
                                                             @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(teamService.inviteMember(teamId, request, currentUser));
    }

    // Ho tro test: xem danh sach nhom cua minh
    @GetMapping
    public ResponseEntity<List<TeamResponse>> getMyTeams(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(teamService.getMyTeams(currentUser));
    }

    // Ho tro test: xem danh sach thanh vien trong 1 nhom
    @GetMapping("/{teamId}/members")
    public ResponseEntity<List<TeamMemberResponse>> getTeamMembers(@PathVariable UUID teamId) {
        return ResponseEntity.ok(teamService.getTeamMembers(teamId));
    }
}