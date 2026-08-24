package com.taskmanager.team;

import com.taskmanager.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeamMemberRepository extends JpaRepository<TeamMember, UUID> {

    List<TeamMember> findByTeamId(UUID teamId);

    Optional<TeamMember> findByUserAndTeamId(User user, UUID teamId);

    boolean existsByUserAndTeamId(User user, UUID teamId);
}