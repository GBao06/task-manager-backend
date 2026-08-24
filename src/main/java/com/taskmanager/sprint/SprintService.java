package com.taskmanager.sprint;

import com.taskmanager.auth.User;
import com.taskmanager.sprint.dto.CreateSprintRequest;
import com.taskmanager.sprint.dto.SprintResponse;
import com.taskmanager.team.Team;
import com.taskmanager.team.TeamMemberRepository;
import com.taskmanager.team.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SprintService {

    @Autowired
    private SprintRepository sprintRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    /**
     * UC09 - Tao sprint moi.
     * Rang buoc: end_date phai sau start_date, khong trung thoi gian voi
     * sprint khac dang active (NOT_STARTED hoac IN_PROGRESS) trong cung nhom.
     */
    public SprintResponse createSprint(CreateSprintRequest request, User currentUser) {
        Team team = teamRepository.findById(request.getTeamId())
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay nhom"));

        ensureIsTeamMember(currentUser, team.getId());

        if (!request.getEndDate().isAfter(request.getStartDate())) {
            throw new IllegalArgumentException("Ngay ket thuc phai sau ngay bat dau");
        }

        List<Sprint> activeSprints = sprintRepository.findByTeamIdAndStatusIn(
                team.getId(), List.of(SprintStatus.NOT_STARTED, SprintStatus.IN_PROGRESS));

        boolean isOverlapping = activeSprints.stream().anyMatch(s ->
                !request.getEndDate().isBefore(s.getStartDate()) &&
                !request.getStartDate().isAfter(s.getEndDate())
        );

        if (isOverlapping) {
            throw new IllegalArgumentException("Thoi gian sprint bi trung voi mot sprint khac dang active trong nhom");
        }

        Sprint sprint = new Sprint(team, request.getName(), request.getStartDate(), request.getEndDate());
        sprintRepository.save(sprint);

        return toResponse(sprint);
    }

    /**
     * UC09 - Cap nhat trang thai vong doi sprint
     * (NOT_STARTED -> IN_PROGRESS -> COMPLETED).
     */
    public SprintResponse updateStatus(UUID sprintId, SprintStatus newStatus, User currentUser) {
        Sprint sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay sprint"));

        ensureIsTeamMember(currentUser, sprint.getTeam().getId());

        sprint.setStatus(newStatus);
        sprintRepository.save(sprint);

        return toResponse(sprint);
    }

    /**
     * Ho tro test: xem danh sach sprint trong 1 nhom.
     */
    public List<SprintResponse> getSprintsByTeam(UUID teamId, User currentUser) {
        ensureIsTeamMember(currentUser, teamId);
        return sprintRepository.findByTeamId(teamId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private void ensureIsTeamMember(User user, UUID teamId) {
        boolean isMember = teamMemberRepository.existsByUserAndTeamId(user, teamId);
        if (!isMember) {
            throw new IllegalArgumentException("Ban khong phai thanh vien cua nhom nay");
        }
    }

    private SprintResponse toResponse(Sprint sprint) {
        return new SprintResponse(
                sprint.getId(),
                sprint.getTeam().getId(),
                sprint.getName(),
                sprint.getStartDate(),
                sprint.getEndDate(),
                sprint.getStatus()
        );
    }
}