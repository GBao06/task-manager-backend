package com.taskmanager.team;

import com.taskmanager.auth.User;
import com.taskmanager.auth.UserRepository;
import com.taskmanager.team.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * UC03 - Tao nhom lam viec.
     * Nguoi tao nhom tu dong tro thanh TEAM_LEAD cua nhom do.
     */
    public TeamResponse createTeam(CreateTeamRequest request, User currentUser) {
        Team team = new Team(request.getName(), request.getDescription(), currentUser);
        teamRepository.save(team);

        // Tu dong them nguoi tao vao team_member voi vai tro TEAM_LEAD
        TeamMember ownerMembership = new TeamMember(currentUser, team, TeamRole.TEAM_LEAD);
        teamMemberRepository.save(ownerMembership);

        return toTeamResponse(team);
    }

    /**
     * UC04 - Moi thanh vien vao nhom.
     * Chi Team Lead cua nhom moi co quyen moi.
     */
    public TeamMemberResponse inviteMember(UUID teamId, InviteMemberRequest request, User currentUser) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay nhom"));

        // Kiem tra quyen: nguoi goi request phai la TEAM_LEAD cua nhom nay
        TeamMember requesterMembership = teamMemberRepository.findByUserAndTeamId(currentUser, teamId)
                .orElseThrow(() -> new IllegalArgumentException("Ban khong phai thanh vien cua nhom nay"));

        if (requesterMembership.getRoleInTeam() != TeamRole.TEAM_LEAD) {
            throw new IllegalArgumentException("Chi Team Lead moi co quyen moi thanh vien");
        }

        // Tim nguoi dung theo email muon moi
        User invitedUser = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Email nay chua co tai khoan trong he thong"));

        // Chan moi trung
        if (teamMemberRepository.existsByUserAndTeamId(invitedUser, teamId)) {
            throw new IllegalArgumentException("Nguoi nay da o trong nhom roi");
        }

        TeamMember newMembership = new TeamMember(invitedUser, team, TeamRole.MEMBER);
        teamMemberRepository.save(newMembership);

        return toMemberResponse(newMembership);
    }

    /**
     * Ho tro test: xem danh sach thanh vien trong 1 nhom.
     */
    public List<TeamMemberResponse> getTeamMembers(UUID teamId) {
        return teamMemberRepository.findByTeamId(teamId).stream()
                .map(this::toMemberResponse)
                .collect(Collectors.toList());
    }

    /**
     * Ho tro test: xem danh sach nhom minh dang o trong do lam owner.
     */
    public List<TeamResponse> getMyTeams(User currentUser) {
        return teamRepository.findAll().stream()
                .filter(team -> team.getOwner().getId().equals(currentUser.getId())
                        || teamMemberRepository.existsByUserAndTeamId(currentUser, team.getId()))
                .map(this::toTeamResponse)
                .collect(Collectors.toList());
    }

    private TeamResponse toTeamResponse(Team team) {
        return new TeamResponse(team.getId(), team.getName(), team.getDescription(), team.getOwner().getEmail());
    }

    private TeamMemberResponse toMemberResponse(TeamMember member) {
        return new TeamMemberResponse(
                member.getId(),
                member.getUser().getId(),
                member.getUser().getFullName(),
                member.getUser().getEmail(),
                member.getRoleInTeam()
        );
    }
}