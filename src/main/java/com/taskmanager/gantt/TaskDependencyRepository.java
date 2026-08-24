package com.taskmanager.gantt;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TaskDependencyRepository extends JpaRepository<TaskDependency, UUID> {

    List<TaskDependency> findBySuccessorTaskId(UUID successorTaskId);

    List<TaskDependency> findByPredecessorTaskId(UUID predecessorTaskId);

    // Lay toan bo quan he phu thuoc cua 1 nhom (join qua Task -> Team)
    List<TaskDependency> findByPredecessorTask_Team_Id(UUID teamId);

    boolean existsByPredecessorTaskIdAndSuccessorTaskId(UUID predecessorTaskId, UUID successorTaskId);
}