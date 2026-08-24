package com.taskmanager.task;

import com.taskmanager.auth.User;
import com.taskmanager.auth.UserRepository;
import com.taskmanager.matrix.MatrixEngineService;
import com.taskmanager.notification.NotificationService;
import com.taskmanager.sprint.Sprint;
import com.taskmanager.sprint.SprintRepository;
import com.taskmanager.task.dto.CreateTaskRequest;
import com.taskmanager.task.dto.TaskResponse;
import com.taskmanager.task.dto.UpdateTaskRequest;
import com.taskmanager.team.Team;
import com.taskmanager.team.TeamMemberRepository;
import com.taskmanager.team.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MatrixEngineService matrixEngineService;

    @Autowired
    private SprintRepository sprintRepository;

    @Autowired
    private NotificationService notificationService;

    public TaskResponse createTask(CreateTaskRequest request, User currentUser) {
        Team team = teamRepository.findById(request.getTeamId())
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay nhom"));

        ensureIsTeamMember(currentUser, team.getId());

        Task task = new Task(team, request.getTitle(), request.getDescription(),
                request.getTaskType(), request.getDueDate());

        if (request.getAssigneeId() != null) {
            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new IllegalArgumentException("Khong tim thay nguoi phu trach"));
            ensureIsTeamMember(assignee, team.getId());
            task.setAssignee(assignee);
        }

        matrixEngineService.applySuggestion(task);

        taskRepository.save(task);

        // Thong bao cho nguoi duoc phan cong (neu co) ngay khi tao task
        if (task.getAssignee() != null) {
            notificationService.notifyAssignment(task.getAssignee(), task);
        }

        return toResponse(task);
    }

    public TaskResponse updateTask(UUID taskId, UpdateTaskRequest request, User currentUser) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay cong viec"));

        ensureIsTeamMember(currentUser, task.getTeam().getId());

        boolean affectsScore = false;

        if (request.getTitle() != null) task.setTitle(request.getTitle());
        if (request.getDescription() != null) task.setDescription(request.getDescription());
        if (request.getTaskType() != null) {
            task.setTaskType(request.getTaskType());
            affectsScore = true;
        }
        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
            affectsScore = true;
        }
        if (request.getStatus() != null) task.setStatus(request.getStatus());

        boolean assigneeChanged = false;
        if (request.getAssigneeId() != null) {
            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new IllegalArgumentException("Khong tim thay nguoi phu trach"));
            ensureIsTeamMember(assignee, task.getTeam().getId());

            // Chi coi la "thay doi" neu khac voi assignee hien tai, tranh spam thong bao
            // moi lan PUT task du khong doi nguoi phu trach
            boolean isDifferentAssignee = task.getAssignee() == null
                    || !task.getAssignee().getId().equals(assignee.getId());

            task.setAssignee(assignee);
            assigneeChanged = isDifferentAssignee;
        }

        if (affectsScore) {
            matrixEngineService.applySuggestion(task);
        }

        taskRepository.save(task);

        if (assigneeChanged) {
            notificationService.notifyAssignment(task.getAssignee(), task);
        }

        return toResponse(task);
    }

    public TaskResponse updateQuadrantManually(UUID taskId, Quadrant newQuadrant, User currentUser) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay cong viec"));

        ensureIsTeamMember(currentUser, task.getTeam().getId());

        task.setQuadrant(newQuadrant);
        taskRepository.save(task);
        return toResponse(task);
    }

    /**
     * UC10 - Dua cong viec vao Sprint.
     * Sprint phai thuoc cung nhom voi task.
     */
    public TaskResponse assignToSprint(UUID taskId, UUID sprintId, User currentUser) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay cong viec"));

        ensureIsTeamMember(currentUser, task.getTeam().getId());

        Sprint sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay sprint"));

        if (!sprint.getTeam().getId().equals(task.getTeam().getId())) {
            throw new IllegalArgumentException("Sprint nay khong thuoc cung nhom voi cong viec");
        }

        task.setSprint(sprint);
        taskRepository.save(task);
        return toResponse(task);
    }

    /**
     * UC10 - Dua cong viec ve lai Backlog (bo khoi sprint).
     */
    public TaskResponse removeFromSprint(UUID taskId, User currentUser) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay cong viec"));

        ensureIsTeamMember(currentUser, task.getTeam().getId());

        task.setSprint(null);
        taskRepository.save(task);
        return toResponse(task);
    }

    public void deleteTask(UUID taskId, User currentUser) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay cong viec"));

        ensureIsTeamMember(currentUser, task.getTeam().getId());
        taskRepository.delete(task);
    }

    public List<TaskResponse> getTasksByTeam(UUID teamId, User currentUser) {
        ensureIsTeamMember(currentUser, teamId);
        return taskRepository.findByTeamId(teamId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private void ensureIsTeamMember(User user, UUID teamId) {
        boolean isMember = teamMemberRepository.existsByUserAndTeamId(user, teamId);
        if (!isMember) {
            throw new IllegalArgumentException("Ban khong phai thanh vien cua nhom nay");
        }
    }

    private TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTeam().getId(),
                task.getSprint() != null ? task.getSprint().getId() : null,
                task.getTitle(),
                task.getDescription(),
                task.getTaskType(),
                task.getDueDate(),
                task.getStatus(),
                task.getAssignee() != null ? task.getAssignee().getEmail() : null,
                task.getQuadrant(),
                task.getUrgencyScore(),
                task.getImportanceScore()
        );
    }
}