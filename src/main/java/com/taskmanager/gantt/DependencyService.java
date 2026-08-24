package com.taskmanager.gantt;

import com.taskmanager.auth.User;
import com.taskmanager.gantt.dto.AddDependencyRequest;
import com.taskmanager.task.Task;
import com.taskmanager.task.TaskRepository;
import com.taskmanager.team.TeamMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DependencyService {

    @Autowired
    private TaskDependencyRepository dependencyRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private CriticalPathService criticalPathService;

    /**
     * UC14 - Them quan he phu thuoc: predecessorTask phai hoan thanh
     * truoc successorTask.
     */
    public void addDependency(UUID successorTaskId, AddDependencyRequest request, User currentUser) {
        Task successor = taskRepository.findById(successorTaskId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay cong viec"));

        Task predecessor = taskRepository.findById(request.getPredecessorTaskId())
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay cong viec phai hoan thanh truoc"));

        ensureIsTeamMember(currentUser, successor.getTeam().getId());

        if (!predecessor.getTeam().getId().equals(successor.getTeam().getId())) {
            throw new IllegalArgumentException("Hai cong viec phai thuoc cung mot nhom");
        }

        if (predecessor.getId().equals(successor.getId())) {
            throw new IllegalArgumentException("Cong viec khong the phu thuoc vao chinh no");
        }

        if (dependencyRepository.existsByPredecessorTaskIdAndSuccessorTaskId(predecessor.getId(), successor.getId())) {
            throw new IllegalArgumentException("Quan he phu thuoc nay da ton tai");
        }

        // Kiem tra vong lap (circular dependency): neu them A -> B ma B da
        // gian tiep phu thuoc vao A thi se tao thanh chu trinh khep kin
        if (wouldCreateCycle(predecessor.getId(), successor.getId(), successor.getTeam().getId())) {
            throw new IllegalArgumentException("Khong the them: quan he nay se tao thanh vong lap phu thuoc khep kin");
        }

        TaskDependency dependency = new TaskDependency(predecessor, successor);
        dependencyRepository.save(dependency);

        // UC18: tinh lai duong gang ngay sau khi thay doi dependency
        criticalPathService.recalculateForTeam(successor.getTeam().getId());
    }

    /**
     * Kiem tra neu them canh predecessor -> successor thi do thi co con la
     * DAG (khong chu trinh) khong. Cach kiem tra: tu successor, di theo
     * cac canh hien co, neu quay lai gap predecessor thi se tao vong lap.
     */
    private boolean wouldCreateCycle(UUID predecessorId, UUID successorId, UUID teamId) {
        List<TaskDependency> allDependencies = dependencyRepository.findByPredecessorTask_Team_Id(teamId);

        Map<UUID, List<UUID>> graph = new HashMap<>();
        for (TaskDependency dep : allDependencies) {
            graph.computeIfAbsent(dep.getPredecessorTask().getId(), k -> new ArrayList<>())
                    .add(dep.getSuccessorTask().getId());
        }

        // DFS tu successorId, neu tim thay predecessorId thi la vong lap
        Deque<UUID> stack = new ArrayDeque<>();
        Set<UUID> visited = new HashSet<>();
        stack.push(successorId);

        while (!stack.isEmpty()) {
            UUID current = stack.pop();
            if (current.equals(predecessorId)) {
                return true;
            }
            if (!visited.add(current)) continue;

            for (UUID next : graph.getOrDefault(current, List.of())) {
                stack.push(next);
            }
        }

        return false;
    }

    /**
     * Xoa quan he phu thuoc.
     */
    public void removeDependency(UUID dependencyId, User currentUser) {
        TaskDependency dependency = dependencyRepository.findById(dependencyId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay quan he phu thuoc"));

        ensureIsTeamMember(currentUser, dependency.getSuccessorTask().getTeam().getId());

        UUID teamId = dependency.getSuccessorTask().getTeam().getId();
        dependencyRepository.delete(dependency);

        criticalPathService.recalculateForTeam(teamId);
    }

    private void ensureIsTeamMember(User user, UUID teamId) {
        boolean isMember = teamMemberRepository.existsByUserAndTeamId(user, teamId);
        if (!isMember) {
            throw new IllegalArgumentException("Ban khong phai thanh vien cua nhom nay");
        }
    }
}