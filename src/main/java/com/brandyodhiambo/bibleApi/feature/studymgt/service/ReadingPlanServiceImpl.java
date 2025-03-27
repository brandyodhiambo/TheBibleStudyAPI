package com.brandyodhiambo.bibleApi.feature.studymgt.service;

import com.brandyodhiambo.bibleApi.exception.ResourceNotFoundException;
import com.brandyodhiambo.bibleApi.feature.groupmgt.models.Group;
import com.brandyodhiambo.bibleApi.feature.groupmgt.repository.GroupRepository;
import com.brandyodhiambo.bibleApi.feature.studymgt.models.ReadingPlan;
import com.brandyodhiambo.bibleApi.feature.studymgt.models.dto.ReadingPlanResponse;
import com.brandyodhiambo.bibleApi.feature.studymgt.repository.ReadingPlanRepository;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.RoleName;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.Users;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.dto.UserSummary;
import com.brandyodhiambo.bibleApi.feature.usermgt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReadingPlanServiceImpl implements ReadingPlanService {

    private final ReadingPlanRepository readingPlanRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ReadingPlanResponse createReadingPlan(Long groupId, String title, String description,
                                               String bibleReferences, LocalDate startDate,
                                               LocalDate endDate, String topics, String username) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId.toString()));
        
        Users user = userRepository.getUserByName(username);
        
        // Check if the user is the group leader or an admin
        boolean isAdmin = user.getRole().stream()
                .anyMatch(role -> role.getName() == RoleName.ROLE_ADMIN);
        boolean isLeader = group.isLeader(user);
        
        if (!isLeader && !isAdmin) {
            throw new AccessDeniedException("Only the group leader or an admin can create reading plans");
        }
        
        ReadingPlan readingPlan = ReadingPlan.builder()
                .title(title)
                .description(description)
                .bibleReferences(bibleReferences)
                .startDate(startDate)
                .endDate(endDate)
                .group(group)
                .createdBy(user)
                .topics(topics)
                .build();
        
        ReadingPlan savedPlan = readingPlanRepository.save(readingPlan);
        
        return mapToReadingPlanResponse(savedPlan);
    }

    @Override
    public ReadingPlanResponse getReadingPlan(Long planId) {
        ReadingPlan plan = readingPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Reading Plan", "id", planId.toString()));
        
        return mapToReadingPlanResponse(plan);
    }

    @Override
    public List<ReadingPlanResponse> getReadingPlansByGroup(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId.toString()));
        
        return readingPlanRepository.findByGroup(group).stream()
                .map(this::mapToReadingPlanResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReadingPlanResponse> getReadingPlansByUser(String username) {
        Users user = userRepository.getUserByName(username);
        
        return readingPlanRepository.findByCreatedBy(user).stream()
                .map(this::mapToReadingPlanResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReadingPlanResponse> searchReadingPlans(String searchTerm) {
        return readingPlanRepository.searchByTerm(searchTerm).stream()
                .map(this::mapToReadingPlanResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReadingPlanResponse> searchReadingPlansInGroup(String searchTerm, Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId.toString()));
        
        return readingPlanRepository.searchByTermInGroup(searchTerm, group).stream()
                .map(this::mapToReadingPlanResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReadingPlanResponse> getReadingPlansByDateRange(LocalDate startDate, LocalDate endDate) {
        return readingPlanRepository.findByStartDateGreaterThanEqualAndEndDateLessThanEqual(startDate, endDate).stream()
                .map(this::mapToReadingPlanResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReadingPlanResponse> getReadingPlansByGroupAndDateRange(Long groupId, LocalDate startDate, LocalDate endDate) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId.toString()));
        
        return readingPlanRepository.findByGroupAndDateRange(group, startDate, endDate).stream()
                .map(this::mapToReadingPlanResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ReadingPlanResponse updateReadingPlan(Long planId, String title, String description,
                                               String bibleReferences, LocalDate startDate,
                                               LocalDate endDate, String topics, String username) {
        ReadingPlan plan = readingPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Reading Plan", "id", planId.toString()));
        
        Users user = userRepository.getUserByName(username);
        
        // Check if the user is the creator, the group leader, or an admin
        boolean isAdmin = user.getRole().stream()
                .anyMatch(role -> role.getName() == RoleName.ROLE_ADMIN);
        boolean isCreator = plan.getCreatedBy().equals(user);
        boolean isLeader = plan.getGroup().isLeader(user);
        
        if (!isCreator && !isLeader && !isAdmin) {
            throw new AccessDeniedException("Only the creator, the group leader, or an admin can update reading plans");
        }
        
        // Update the plan fields if provided
        if (title != null) {
            plan.setTitle(title);
        }
        
        if (description != null) {
            plan.setDescription(description);
        }
        
        if (bibleReferences != null) {
            plan.setBibleReferences(bibleReferences);
        }
        
        if (startDate != null) {
            plan.setStartDate(startDate);
        }
        
        if (endDate != null) {
            plan.setEndDate(endDate);
        }
        
        if (topics != null) {
            plan.setTopics(topics);
        }
        
        ReadingPlan updatedPlan = readingPlanRepository.save(plan);
        
        return mapToReadingPlanResponse(updatedPlan);
    }

    @Override
    @Transactional
    public void deleteReadingPlan(Long planId, String username) {
        ReadingPlan plan = readingPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Reading Plan", "id", planId.toString()));
        
        Users user = userRepository.getUserByName(username);
        
        // Check if the user is the creator, the group leader, or an admin
        boolean isAdmin = user.getRole().stream()
                .anyMatch(role -> role.getName() == RoleName.ROLE_ADMIN);
        boolean isCreator = plan.getCreatedBy().equals(user);
        boolean isLeader = plan.getGroup().isLeader(user);
        
        if (!isCreator && !isLeader && !isAdmin) {
            throw new AccessDeniedException("Only the creator, the group leader, or an admin can delete reading plans");
        }
        
        readingPlanRepository.delete(plan);
    }
    
    // Helper method to map ReadingPlan entity to ReadingPlanResponse DTO
    private ReadingPlanResponse mapToReadingPlanResponse(ReadingPlan plan) {
        return ReadingPlanResponse.builder()
                .id(plan.getId())
                .title(plan.getTitle())
                .description(plan.getDescription())
                .bibleReferences(plan.getBibleReferences())
                .startDate(plan.getStartDate())
                .endDate(plan.getEndDate())
                .groupId(plan.getGroup().getId())
                .groupName(plan.getGroup().getName())
                .createdBy(mapToUserSummary(plan.getCreatedBy()))
                .topics(plan.getTopics())
                .createdAt(plan.getCreatedAt())
                .updatedAt(plan.getUpdatedAt())
                .build();
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