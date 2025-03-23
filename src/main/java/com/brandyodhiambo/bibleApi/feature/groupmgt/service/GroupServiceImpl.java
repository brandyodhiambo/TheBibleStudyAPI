package com.brandyodhiambo.bibleApi.feature.groupmgt.service;

import com.brandyodhiambo.bibleApi.exception.ResourceNotFoundException;
import com.brandyodhiambo.bibleApi.feature.groupmgt.models.Group;
import com.brandyodhiambo.bibleApi.feature.groupmgt.models.dto.CreateGroupRequest;
import com.brandyodhiambo.bibleApi.feature.groupmgt.models.dto.GroupResponse;
import com.brandyodhiambo.bibleApi.feature.groupmgt.models.dto.UpdateGroupRequest;
import com.brandyodhiambo.bibleApi.feature.groupmgt.repository.GroupRepository;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.RoleName;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.Users;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.dto.UserSummary;
import com.brandyodhiambo.bibleApi.feature.usermgt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public GroupResponse createGroup(CreateGroupRequest request, String username) {
        Users leader = userRepository.getUserByName(username);
        
        // Check if the user has the ROLE_LEADER or ROLE_ADMIN role
        boolean hasLeaderRole = leader.getRole().stream()
                .anyMatch(role -> role.getName() == RoleName.ROLE_LEADER || role.getName() == RoleName.ROLE_ADMIN);
        
        if (!hasLeaderRole) {
            throw new AccessDeniedException("Only group leaders or admins can create groups");
        }
        
        // Check if a group with the same name already exists
        if (groupRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("A group with this name already exists");
        }
        
        Group group = new Group();
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        group.setLocation(request.getLocation());
        group.setMeetingTime(request.getMeetingTime());
        group.setType(request.getType());
        group.setLeader(leader);
        
        // Add the leader as a member
        group.addMember(leader);
        
        Group savedGroup = groupRepository.save(group);
        
        return mapToGroupResponse(savedGroup);
    }

    @Override
    @Transactional
    public GroupResponse updateGroup(Long groupId, UpdateGroupRequest request, String username) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId.toString()));
        
        Users user = userRepository.getUserByName(username);
        
        // Check if the user is the group leader or an admin
        boolean isAdmin = user.getRole().stream()
                .anyMatch(role -> role.getName() == RoleName.ROLE_ADMIN);
        
        if (!group.isLeader(user) && !isAdmin) {
            throw new AccessDeniedException("Only the group leader or an admin can update the group");
        }
        
        // Update the group fields if provided
        if (request.getName() != null) {
            group.setName(request.getName());
        }
        
        if (request.getDescription() != null) {
            group.setDescription(request.getDescription());
        }
        
        if (request.getLocation() != null) {
            group.setLocation(request.getLocation());
        }
        
        if (request.getMeetingTime() != null) {
            group.setMeetingTime(request.getMeetingTime());
        }
        
        if (request.getType() != null) {
            group.setType(request.getType());
        }
        
        Group updatedGroup = groupRepository.save(group);
        
        return mapToGroupResponse(updatedGroup);
    }

    @Override
    @Transactional
    public void deleteGroup(Long groupId, String username) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId.toString()));
        
        Users user = userRepository.getUserByName(username);
        
        // Check if the user is the group leader or an admin
        boolean isAdmin = user.getRole().stream()
                .anyMatch(role -> role.getName() == RoleName.ROLE_ADMIN);
        
        if (!group.isLeader(user) && !isAdmin) {
            throw new AccessDeniedException("Only the group leader or an admin can delete the group");
        }
        
        groupRepository.delete(group);
    }

    @Override
    public GroupResponse getGroup(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId.toString()));
        
        return mapToGroupResponse(group);
    }

    @Override
    public List<GroupResponse> getAllGroups() {
        return groupRepository.findAll().stream()
                .map(this::mapToGroupResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<GroupResponse> getGroupsByLeader(String username) {
        Users leader = userRepository.getUserByName(username);
        
        return groupRepository.findByLeader(leader).stream()
                .map(this::mapToGroupResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<GroupResponse> getGroupsByMember(String username) {
        Users member = userRepository.getUserByName(username);
        
        return groupRepository.findByMember(member).stream()
                .map(this::mapToGroupResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public GroupResponse addMember(Long groupId, String username, String requesterUsername) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId.toString()));
        
        Users user = userRepository.getUserByName(username);
        Users requester = userRepository.getUserByName(requesterUsername);
        
        // Check if the requester is the group leader or an admin
        boolean isAdmin = requester.getRole().stream()
                .anyMatch(role -> role.getName() == RoleName.ROLE_ADMIN);
        
        if (!group.isLeader(requester) && !isAdmin) {
            throw new AccessDeniedException("Only the group leader or an admin can add members");
        }
        
        // Check if the user is already a member
        if (group.isMember(user)) {
            throw new IllegalArgumentException("User is already a member of this group");
        }
        
        group.addMember(user);
        Group updatedGroup = groupRepository.save(group);
        
        return mapToGroupResponse(updatedGroup);
    }

    @Override
    @Transactional
    public GroupResponse removeMember(Long groupId, String username, String requesterUsername) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId.toString()));
        
        Users user = userRepository.getUserByName(username);
        Users requester = userRepository.getUserByName(requesterUsername);
        
        // Check if the requester is the group leader, the user being removed, or an admin
        boolean isAdmin = requester.getRole().stream()
                .anyMatch(role -> role.getName() == RoleName.ROLE_ADMIN);
        
        if (!group.isLeader(requester) && !requester.equals(user) && !isAdmin) {
            throw new AccessDeniedException("Only the group leader, the user being removed, or an admin can remove members");
        }
        
        // Check if the user is a member
        if (!group.isMember(user)) {
            throw new IllegalArgumentException("User is not a member of this group");
        }
        
        // Check if the user is the leader
        if (group.isLeader(user)) {
            throw new IllegalArgumentException("The group leader cannot be removed from the group");
        }
        
        group.removeMember(user);
        Group updatedGroup = groupRepository.save(group);
        
        return mapToGroupResponse(updatedGroup);
    }

    @Override
    @Transactional
    public GroupResponse joinGroup(Long groupId, String username) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId.toString()));
        
        Users user = userRepository.getUserByName(username);
        
        // Check if the user is already a member
        if (group.isMember(user)) {
            throw new IllegalArgumentException("You are already a member of this group");
        }
        
        group.addMember(user);
        Group updatedGroup = groupRepository.save(group);
        
        return mapToGroupResponse(updatedGroup);
    }

    @Override
    @Transactional
    public GroupResponse leaveGroup(Long groupId, String username) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId.toString()));
        
        Users user = userRepository.getUserByName(username);
        
        // Check if the user is a member
        if (!group.isMember(user)) {
            throw new IllegalArgumentException("You are not a member of this group");
        }
        
        // Check if the user is the leader
        if (group.isLeader(user)) {
            throw new IllegalArgumentException("The group leader cannot leave the group");
        }
        
        group.removeMember(user);
        Group updatedGroup = groupRepository.save(group);
        
        return mapToGroupResponse(updatedGroup);
    }
    
    // Helper method to map Group entity to GroupResponse DTO
    private GroupResponse mapToGroupResponse(Group group) {
        UserSummary leaderSummary = mapToUserSummary(group.getLeader());
        
        Set<UserSummary> memberSummaries = group.getMembers().stream()
                .map(this::mapToUserSummary)
                .collect(Collectors.toSet());
        
        return new GroupResponse(
                group.getId(),
                group.getName(),
                group.getDescription(),
                group.getLocation(),
                group.getMeetingTime(),
                group.getType(),
                leaderSummary,
                memberSummaries,
                group.getCreatedAt(),
                group.getUpdatedAt(),
                group.getMembers().size()
        );
    }
    
    // Helper method to map Users entity to UserSummary DTO
    private UserSummary mapToUserSummary(Users user) {
        return new UserSummary(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail()
        );
    }
}