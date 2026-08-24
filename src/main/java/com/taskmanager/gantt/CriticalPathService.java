package com.taskmanager.gantt;

import com.taskmanager.task.Task;
import com.taskmanager.task.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * UC18 - Xac dinh duong gang (Critical Path) cua du an/nhom.
 *
 * Thuat toan don gian hoa (khong dung CPM chuan voi forward/backward pass
 * va slack time day du, phu hop muc do niên luận):
 * 1. Xay dung do thi phu thuoc (dependency graph) tu TaskDependency.
 * 2. Voi moi task, tinh "earliest finish" bang duration_days cong don theo
 *    tung nhanh (dynamic programming tren DAG, dua vao thu tu topo).
 * 3. Nhanh co tong thoi gian cong don lon nhat la duong gang.
 * 4. Danh dau is_critical = true cho tat ca task thuoc nhanh do.
 */
@Service
public class CriticalPathService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskDependencyRepository dependencyRepository;

    /**
     * Tinh lai duong gang cho toan bo task trong 1 nhom, cap nhat is_critical.
     */
    public void recalculateForTeam(UUID teamId) {
        List<Task> tasks = taskRepository.findByTeamId(teamId);
        List<TaskDependency> dependencies = dependencyRepository.findByPredecessorTask_Team_Id(teamId);

        // Reset is_critical truoc khi tinh lai
        for (Task task : tasks) {
            task.setCritical(false);
        }

        if (tasks.isEmpty()) {
            taskRepository.saveAll(tasks);
            return;
        }

        // Map: taskId -> danh sach cac task ke tiep (successor)
        Map<UUID, List<UUID>> successorsMap = new HashMap<>();
        // Map: taskId -> so luong task truoc no (in-degree), dung de sap xep topo
        Map<UUID, Integer> inDegree = new HashMap<>();

        for (Task task : tasks) {
            successorsMap.put(task.getId(), new ArrayList<>());
            inDegree.put(task.getId(), 0);
        }

        for (TaskDependency dep : dependencies) {
            UUID predId = dep.getPredecessorTask().getId();
            UUID succId = dep.getSuccessorTask().getId();
            if (successorsMap.containsKey(predId) && inDegree.containsKey(succId)) {
                successorsMap.get(predId).add(succId);
                inDegree.put(succId, inDegree.get(succId) + 1);
            }
        }

        // Sap xep topo (Kahn's algorithm) de dam bao xu ly dung thu tu phu thuoc
        List<UUID> topoOrder = new ArrayList<>();
        Deque<UUID> queue = new ArrayDeque<>();
        for (Map.Entry<UUID, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) queue.add(entry.getKey());
        }
        Map<UUID, Integer> remainingInDegree = new HashMap<>(inDegree);
        while (!queue.isEmpty()) {
            UUID current = queue.poll();
            topoOrder.add(current);
            for (UUID next : successorsMap.get(current)) {
                remainingInDegree.put(next, remainingInDegree.get(next) - 1);
                if (remainingInDegree.get(next) == 0) queue.add(next);
            }
        }

        // Neu topoOrder khong du (co chu trinh) -> bo qua tinh critical path de an toan
        if (topoOrder.size() != tasks.size()) {
            taskRepository.saveAll(tasks);
            return;
        }

        Map<UUID, Task> taskById = new HashMap<>();
        for (Task task : tasks) taskById.put(task.getId(), task);

        // earliestFinish[taskId] = tong duration_days cong don tu task goc den task nay
        Map<UUID, Integer> earliestFinish = new HashMap<>();
        // predecessorOnLongestPath: de truy vet lai nhanh dai nhat
        Map<UUID, UUID> bestPredecessor = new HashMap<>();

        for (UUID taskId : topoOrder) {
            int duration = getDuration(taskById.get(taskId));
            earliestFinish.putIfAbsent(taskId, duration);
        }

        for (UUID current : topoOrder) {
            int currentFinish = earliestFinish.get(current);
            for (UUID next : successorsMap.get(current)) {
                int candidateFinish = currentFinish + getDuration(taskById.get(next));
                if (candidateFinish > earliestFinish.getOrDefault(next, 0)) {
                    earliestFinish.put(next, candidateFinish);
                    bestPredecessor.put(next, current);
                }
            }
        }

        // Tim task co earliestFinish lon nhat -> diem cuoi cua duong gang
        UUID endOfCriticalPath = null;
        int maxFinish = -1;
        for (Map.Entry<UUID, Integer> entry : earliestFinish.entrySet()) {
            if (entry.getValue() > maxFinish) {
                maxFinish = entry.getValue();
                endOfCriticalPath = entry.getKey();
            }
        }

        // Truy vet nguoc lai tu diem cuoi de danh dau toan bo duong gang
        UUID cursor = endOfCriticalPath;
        while (cursor != null) {
            taskById.get(cursor).setCritical(true);
            cursor = bestPredecessor.get(cursor);
        }

        taskRepository.saveAll(tasks);
    }

    private int getDuration(Task task) {
        return task.getDurationDays() != null ? task.getDurationDays() : 0;
    }
}