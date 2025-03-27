package com.brandyodhiambo.bibleApi.feature.prayermgt.service;

import com.brandyodhiambo.bibleApi.feature.groupmgt.models.Group;
import com.brandyodhiambo.bibleApi.feature.groupmgt.repository.GroupRepository;
import com.brandyodhiambo.bibleApi.feature.prayermgt.models.PrayerRequest;
import com.brandyodhiambo.bibleApi.feature.prayermgt.models.dto.PrayerRequestResponse;
import com.brandyodhiambo.bibleApi.feature.prayermgt.repository.PrayerRequestRepository;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.Users;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrayerRequestServiceImpl implements PrayerRequestService {

    private final PrayerRequestRepository prayerRequestRepository;
    private final GroupRepository groupRepository;

    @Override
    public PrayerRequestResponse createPrayerRequest(Long groupId, String title, String description) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Users currentUser = (Users) authentication.getPrincipal();

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found with id: " + groupId));

        // Check if user is a member of the group
        if (!group.isMember(currentUser) && !group.isLeader(currentUser)) {
            throw new IllegalStateException("You must be a member of the group to create prayer requests");
        }

        PrayerRequest prayerRequest = PrayerRequest.builder()
                .title(title)
                .description(description)
                .group(group)
                .user(currentUser)
                .answered(false)
                .build();

        PrayerRequest savedPrayerRequest = prayerRequestRepository.save(prayerRequest);
        return mapToResponse(savedPrayerRequest);
    }

    @Override
    public List<PrayerRequestResponse> getGroupPrayerRequests(Long groupId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Users currentUser = (Users) authentication.getPrincipal();

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found with id: " + groupId));

        // Check if user is a member of the group
        if (!group.isMember(currentUser) && !group.isLeader(currentUser)) {
            throw new IllegalStateException("You must be a member of the group to view prayer requests");
        }

        List<PrayerRequest> prayerRequests = prayerRequestRepository.findByGroupIdOrderByCreatedAtDesc(groupId);
        return prayerRequests.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PrayerRequestResponse> getGroupPrayerRequestsByAnswered(Long groupId, boolean answered) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Users currentUser = (Users) authentication.getPrincipal();

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found with id: " + groupId));

        // Check if user is a member of the group
        if (!group.isMember(currentUser) && !group.isLeader(currentUser)) {
            throw new IllegalStateException("You must be a member of the group to view prayer requests");
        }

        List<PrayerRequest> prayerRequests = prayerRequestRepository.findByGroupIdAndAnsweredOrderByCreatedAtDesc(groupId, answered);
        return prayerRequests.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PrayerRequestResponse getPrayerRequest(Long prayerRequestId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Users currentUser = (Users) authentication.getPrincipal();

        PrayerRequest prayerRequest = prayerRequestRepository.findById(prayerRequestId)
                .orElseThrow(() -> new EntityNotFoundException("Prayer request not found with id: " + prayerRequestId));

        Group group = prayerRequest.getGroup();

        // Check if user is a member of the group
        if (!group.isMember(currentUser) && !group.isLeader(currentUser)) {
            throw new IllegalStateException("You must be a member of the group to view this prayer request");
        }

        return mapToResponse(prayerRequest);
    }

    @Override
    public PrayerRequestResponse updatePrayerRequest(Long prayerRequestId, String title, String description) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Users currentUser = (Users) authentication.getPrincipal();

        PrayerRequest prayerRequest = prayerRequestRepository.findById(prayerRequestId)
                .orElseThrow(() -> new EntityNotFoundException("Prayer request not found with id: " + prayerRequestId));

        // Check if user is the author of the prayer request
        if (!prayerRequest.getUser().equals(currentUser)) {
            throw new IllegalStateException("You can only update your own prayer requests");
        }

        prayerRequest.setTitle(title);
        prayerRequest.setDescription(description);
        PrayerRequest updatedPrayerRequest = prayerRequestRepository.save(prayerRequest);
        return mapToResponse(updatedPrayerRequest);
    }

    @Override
    public PrayerRequestResponse markAsAnswered(Long prayerRequestId, String testimony) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Users currentUser = (Users) authentication.getPrincipal();

        PrayerRequest prayerRequest = prayerRequestRepository.findById(prayerRequestId)
                .orElseThrow(() -> new EntityNotFoundException("Prayer request not found with id: " + prayerRequestId));

        // Check if user is the author of the prayer request
        if (!prayerRequest.getUser().equals(currentUser)) {
            throw new IllegalStateException("You can only mark your own prayer requests as answered");
        }

        prayerRequest.setAnswered(true);
        prayerRequest.setTestimony(testimony);
        PrayerRequest updatedPrayerRequest = prayerRequestRepository.save(prayerRequest);
        return mapToResponse(updatedPrayerRequest);
    }

    @Override
    public void deletePrayerRequest(Long prayerRequestId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Users currentUser = (Users) authentication.getPrincipal();

        PrayerRequest prayerRequest = prayerRequestRepository.findById(prayerRequestId)
                .orElseThrow(() -> new EntityNotFoundException("Prayer request not found with id: " + prayerRequestId));

        // Check if user is the author of the prayer request or the group leader
        if (!prayerRequest.getUser().equals(currentUser) && !prayerRequest.getGroup().isLeader(currentUser)) {
            throw new IllegalStateException("You can only delete your own prayer requests or as a group leader");
        }

        prayerRequestRepository.delete(prayerRequest);
    }

    @Override
    public List<PrayerRequestResponse> getUserPrayerRequests() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Users currentUser = (Users) authentication.getPrincipal();

        List<PrayerRequest> prayerRequests = prayerRequestRepository.findByUserOrderByCreatedAtDesc(currentUser);
        return prayerRequests.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PrayerRequestResponse> getUserPrayerRequestsByAnswered(boolean answered) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Users currentUser = (Users) authentication.getPrincipal();

        List<PrayerRequest> prayerRequests = prayerRequestRepository.findByUserAndAnsweredOrderByCreatedAtDesc(currentUser, answered);
        return prayerRequests.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private PrayerRequestResponse mapToResponse(PrayerRequest prayerRequest) {
        return PrayerRequestResponse.builder()
                .id(prayerRequest.getId())
                .title(prayerRequest.getTitle())
                .description(prayerRequest.getDescription())
                .groupId(prayerRequest.getGroup().getId())
                .groupName(prayerRequest.getGroup().getName())
                .userId(prayerRequest.getUser().getId())
                .username(prayerRequest.getUser().getUsername())
                .answered(prayerRequest.isAnswered())
                .testimony(prayerRequest.getTestimony())
                .createdAt(prayerRequest.getCreatedAt())
                .updatedAt(prayerRequest.getUpdatedAt())
                .build();
    }
}