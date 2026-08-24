package com.taskmanager.gantt;

import com.taskmanager.auth.User;
import com.taskmanager.gantt.dto.AddDependencyRequest;
import com.taskmanager.gantt.dto.GanttTaskResponse;
import com.taskmanager.gantt.dto.UpdateScheduleRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class GanttController {

    @Autowired
    private GanttService ganttService;

    @Autowired
    private DependencyService dependencyService;

    // UC13: Xem tien do cong viec dang Gantt chart cho 1 nhom
    @GetMapping("/api/teams/{teamId}/gantt")
    public ResponseEntity<List<GanttTaskResponse>> getGanttData(@PathVariable UUID teamId,
                                                                  @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ganttService.getGanttData(teamId, currentUser));
    }

    // Cap nhat lich trinh cua 1 task (start date, duration, milestone)
    @PutMapping("/api/tasks/{taskId}/schedule")
    public ResponseEntity<GanttTaskResponse> updateSchedule(@PathVariable UUID taskId,
                                                              @RequestBody UpdateScheduleRequest request,
                                                              @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ganttService.updateSchedule(taskId, request, currentUser));
    }

    // UC14: Them quan he phu thuoc cho 1 task
    @PostMapping("/api/tasks/{taskId}/dependencies")
    public ResponseEntity<Void> addDependency(@PathVariable UUID taskId,
                                               @Valid @RequestBody AddDependencyRequest request,
                                               @AuthenticationPrincipal User currentUser) {
        dependencyService.addDependency(taskId, request, currentUser);
        return ResponseEntity.ok().build();
    }

    // UC14: Xoa quan he phu thuoc
    @DeleteMapping("/api/dependencies/{dependencyId}")
    public ResponseEntity<Void> removeDependency(@PathVariable UUID dependencyId,
                                                  @AuthenticationPrincipal User currentUser) {
        dependencyService.removeDependency(dependencyId, currentUser);
        return ResponseEntity.noContent().build();
    }
}