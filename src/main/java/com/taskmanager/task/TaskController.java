package com.taskmanager.task;

import com.taskmanager.auth.User;
import com.taskmanager.task.dto.AssignSprintRequest;
import com.taskmanager.task.dto.CreateTaskRequest;
import com.taskmanager.task.dto.TaskResponse;
import com.taskmanager.task.dto.UpdateQuadrantRequest;
import com.taskmanager.task.dto.UpdateTaskRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    // UC05: Tao cong viec moi
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody CreateTaskRequest request,
                                                     @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(taskService.createTask(request, currentUser));
    }

    // UC06: Chinh sua cong viec
    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable UUID taskId,
                                                     @RequestBody UpdateTaskRequest request,
                                                     @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(taskService.updateTask(taskId, request, currentUser));
    }

    // UC07: Nguoi dung tu tay keo-tha doi quadrant
    @PutMapping("/{taskId}/quadrant")
    public ResponseEntity<TaskResponse> updateQuadrant(@PathVariable UUID taskId,
                                                         @Valid @RequestBody UpdateQuadrantRequest request,
                                                         @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(taskService.updateQuadrantManually(taskId, request.getQuadrant(), currentUser));
    }

    // UC10: Dua cong viec vao Sprint
    @PutMapping("/{taskId}/sprint")
    public ResponseEntity<TaskResponse> assignToSprint(@PathVariable UUID taskId,
                                                         @Valid @RequestBody AssignSprintRequest request,
                                                         @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(taskService.assignToSprint(taskId, request.getSprintId(), currentUser));
    }

    // UC10: Dua cong viec ve lai Backlog
    @DeleteMapping("/{taskId}/sprint")
    public ResponseEntity<TaskResponse> removeFromSprint(@PathVariable UUID taskId,
                                                           @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(taskService.removeFromSprint(taskId, currentUser));
    }

    // UC06: Xoa cong viec
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID taskId,
                                            @AuthenticationPrincipal User currentUser) {
        taskService.deleteTask(taskId, currentUser);
        return ResponseEntity.noContent().build();
    }

    // Ho tro test: xem danh sach cong viec trong 1 nhom
    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<TaskResponse>> getTasksByTeam(@PathVariable UUID teamId,
                                                               @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(taskService.getTasksByTeam(teamId, currentUser));
    }
}