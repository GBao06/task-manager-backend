package com.taskmanager.gantt;

import com.taskmanager.auth.User;
import com.taskmanager.gantt.dto.GanttTaskResponse;
import com.taskmanager.gantt.dto.UpdateScheduleRequest;
import com.taskmanager.task.Task;
import com.taskmanager.task.TaskRepository;
import com.taskmanager.team.TeamMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GanttService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskDependencyRepository dependencyRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private CriticalPathService criticalPathService;

    /**
     * UC13 - Xem tien do cong viec dang Gantt chart.
     * Tra ve danh sach task kem thoi gian, milestone, critical path,
     * va danh sach cac task phu thuoc (de ve duong noi tren bieu do).
     */
    public List<GanttTaskResponse> getGanttData(UUID teamId, User currentUser) {
        ensureIsTeamMember(currentUser, teamId);

        List<Task> tasks = taskRepository.findByTeamId(teamId);

        return tasks.stream().map(task -> {
            List<UUID> dependsOn = dependencyRepository.findBySuccessorTaskId(task.getId())
                    .stream()
                    .map(dep -> dep.getPredecessorTask().getId())
                    .collect(Collectors.toList());

            return new GanttTaskResponse(
                    task.getId(),
                    task.getTitle(),
                    task.getStartDate(),
                    task.getDueDate(),
                    task.getDurationDays(),
                    task.getStatus(),
                    task.getQuadrant(),
                    task.isMilestone(),
                    task.isCritical(),
                    dependsOn
            );
        }).collect(Collectors.toList());
    }

    /**
     * Cap nhat thong tin lich trinh (startDate, durationDays, isMilestone)
     * cua 1 task - can thiet de ve dung tren Gantt chart. Neu doi
     * durationDays, tinh lai duong gang (UC18).
     */
    public GanttTaskResponse updateSchedule(UUID taskId, UpdateScheduleRequest request, User currentUser) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay cong viec"));

        ensureIsTeamMember(currentUser, task.getTeam().getId());

        boolean durationChanged = false;

        if (request.getStartDate() != null) task.setStartDate(request.getStartDate());
        if (request.getDurationDays() != null) {
            task.setDurationDays(request.getDurationDays());
            durationChanged = true;
        }
        if (request.getIsMilestone() != null) task.setMilestone(request.getIsMilestone());

        taskRepository.save(task);

        if (durationChanged) {
            criticalPathService.recalculateForTeam(task.getTeam().getId());
        }

        List<UUID> dependsOn = dependencyRepository.findBySuccessorTaskId(task.getId())
                .stream()
                .map(dep -> dep.getPredecessorTask().getId())
                .collect(Collectors.toList());

        return new GanttTaskResponse(
                task.getId(), task.getTitle(), task.getStartDate(), task.getDueDate(),
                task.getDurationDays(), task.getStatus(), task.getQuadrant(),
                task.isMilestone(), task.isCritical(), dependsOn
        );
    }

    private void ensureIsTeamMember(User user, UUID teamId) {
        boolean isMember = teamMemberRepository.existsByUserAndTeamId(user, teamId);
        if (!isMember) {
            throw new IllegalArgumentException("Ban khong phai thanh vien cua nhom nay");
        }
    }
}