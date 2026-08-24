package com.taskmanager.gantt;

import com.taskmanager.task.Task;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "task_dependency", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"predecessor_task_id", "successor_task_id"})
})
public class TaskDependency {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    // Task phai hoan thanh truoc
    @ManyToOne
    @JoinColumn(name = "predecessor_task_id", nullable = false)
    private Task predecessorTask;

    // Task phu thuoc, chi bat dau sau khi predecessor hoan thanh
    @ManyToOne
    @JoinColumn(name = "successor_task_id", nullable = false)
    private Task successorTask;

    public TaskDependency() {
    }

    public TaskDependency(Task predecessorTask, Task successorTask) {
        this.predecessorTask = predecessorTask;
        this.successorTask = successorTask;
    }

    public UUID getId() {
        return id;
    }

    public Task getPredecessorTask() {
        return predecessorTask;
    }

    public void setPredecessorTask(Task predecessorTask) {
        this.predecessorTask = predecessorTask;
    }

    public Task getSuccessorTask() {
        return successorTask;
    }

    public void setSuccessorTask(Task successorTask) {
        this.successorTask = successorTask;
    }
}