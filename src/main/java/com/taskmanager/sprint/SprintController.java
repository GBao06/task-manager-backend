package com.taskmanager.sprint;

import com.taskmanager.auth.User;
import com.taskmanager.sprint.dto.CreateSprintRequest;
import com.taskmanager.sprint.dto.SprintResponse;
import com.taskmanager.sprint.dto.UpdateSprintStatusRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sprints")
public class SprintController {

    @Autowired
    private SprintService sprintService;

    // UC09: Tao sprint moi
    @PostMapping
    public ResponseEntity<SprintResponse> createSprint(@Valid @RequestBody CreateSprintRequest request,
                                                         @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(sprintService.createSprint(request, currentUser));
    }

    // UC09: Cap nhat trang thai sprint
    @PutMapping("/{sprintId}/status")
    public ResponseEntity<SprintResponse> updateStatus(@PathVariable UUID sprintId,
                                                         @Valid @RequestBody UpdateSprintStatusRequest request,
                                                         @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(sprintService.updateStatus(sprintId, request.getStatus(), currentUser));
    }

    // Ho tro test: xem danh sach sprint trong 1 nhom
    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<SprintResponse>> getSprintsByTeam(@PathVariable UUID teamId,
                                                                   @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(sprintService.getSprintsByTeam(teamId, currentUser));
    }
}