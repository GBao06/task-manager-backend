package com.taskmanager.sprint;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SprintRepository extends JpaRepository<Sprint, UUID> {

    List<Sprint> findByTeamId(UUID teamId);

    // Ho tro kiem tra trung thoi gian giua cac sprint dang active trong cung 1 nhom
    List<Sprint> findByTeamIdAndStatusIn(UUID teamId, List<SprintStatus> statuses);
}