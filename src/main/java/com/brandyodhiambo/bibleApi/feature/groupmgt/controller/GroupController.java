package com.brandyodhiambo.bibleApi.feature.groupmgt.controller;

import com.brandyodhiambo.bibleApi.feature.groupmgt.models.dto.CreateGroupRequest;
import com.brandyodhiambo.bibleApi.feature.groupmgt.models.dto.GroupMemberRequest;
import com.brandyodhiambo.bibleApi.feature.groupmgt.models.dto.GroupResponse;
import com.brandyodhiambo.bibleApi.feature.groupmgt.models.dto.UpdateGroupRequest;
import com.brandyodhiambo.bibleApi.feature.groupmgt.service.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    @PreAuthorize("hasRole('LEADER') or hasRole('ADMIN')")
    public ResponseEntity<GroupResponse> createGroup(
            @Valid @RequestBody CreateGroupRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        GroupResponse group = groupService.createGroup(request, userDetails.getUsername());
        return new ResponseEntity<>(group, HttpStatus.CREATED);
    }

    @PutMapping("/{groupId}")
    public ResponseEntity<GroupResponse> updateGroup(
            @PathVariable Long groupId,
            @Valid @RequestBody UpdateGroupRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        GroupResponse group = groupService.updateGroup(groupId, request, userDetails.getUsername());
        return ResponseEntity.ok(group);
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> deleteGroup(
            @PathVariable Long groupId,
            @AuthenticationPrincipal UserDetails userDetails) {
        groupService.deleteGroup(groupId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<GroupResponse> getGroup(@PathVariable Long groupId) {
        GroupResponse group = groupService.getGroup(groupId);
        return ResponseEntity.ok(group);
    }

    @GetMapping
    public ResponseEntity<List<GroupResponse>> getAllGroups() {
        List<GroupResponse> groups = groupService.getAllGroups();
        return ResponseEntity.ok(groups);
    }

    @GetMapping("/leader")
    public ResponseEntity<List<GroupResponse>> getGroupsByLeader(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<GroupResponse> groups = groupService.getGroupsByLeader(userDetails.getUsername());
        return ResponseEntity.ok(groups);
    }

    @GetMapping("/member")
    public ResponseEntity<List<GroupResponse>> getGroupsByMember(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<GroupResponse> groups = groupService.getGroupsByMember(userDetails.getUsername());
        return ResponseEntity.ok(groups);
    }

    @PostMapping("/{groupId}/members")
    @PreAuthorize("hasRole('LEADER') or hasRole('ADMIN')")
    public ResponseEntity<GroupResponse> addMember(
            @PathVariable Long groupId,
            @Valid @RequestBody GroupMemberRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        GroupResponse group = groupService.addMember(groupId, request.getUsername(), userDetails.getUsername());
        return ResponseEntity.ok(group);
    }

    @DeleteMapping("/{groupId}/members")
    public ResponseEntity<GroupResponse> removeMember(
            @PathVariable Long groupId,
            @Valid @RequestBody GroupMemberRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        GroupResponse group = groupService.removeMember(groupId, request.getUsername(), userDetails.getUsername());
        return ResponseEntity.ok(group);
    }

    @PostMapping("/{groupId}/join")
    public ResponseEntity<GroupResponse> joinGroup(
            @PathVariable Long groupId,
            @AuthenticationPrincipal UserDetails userDetails) {
        GroupResponse group = groupService.joinGroup(groupId, userDetails.getUsername());
        return ResponseEntity.ok(group);
    }

    @PostMapping("/{groupId}/leave")
    public ResponseEntity<GroupResponse> leaveGroup(
            @PathVariable Long groupId,
            @AuthenticationPrincipal UserDetails userDetails) {
        GroupResponse group = groupService.leaveGroup(groupId, userDetails.getUsername());
        return ResponseEntity.ok(group);
    }
}