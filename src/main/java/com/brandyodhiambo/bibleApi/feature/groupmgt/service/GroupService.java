package com.brandyodhiambo.bibleApi.feature.groupmgt.service;

import com.brandyodhiambo.bibleApi.feature.groupmgt.models.dto.CreateGroupRequest;
import com.brandyodhiambo.bibleApi.feature.groupmgt.models.dto.GroupResponse;
import com.brandyodhiambo.bibleApi.feature.groupmgt.models.dto.UpdateGroupRequest;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface GroupService {
    
    /**
     * Create a new group with the current user as the leader
     * @param request the group creation request
     * @param username the username of the group leader
     * @return the created group
     */
    GroupResponse createGroup(CreateGroupRequest request, String username);
    
    /**
     * Update an existing group
     * @param groupId the ID of the group to update
     * @param request the group update request
     * @param username the username of the user making the request (must be the group leader or an admin)
     * @return the updated group
     */
    GroupResponse updateGroup(Long groupId, UpdateGroupRequest request, String username);
    
    /**
     * Delete a group
     * @param groupId the ID of the group to delete
     * @param username the username of the user making the request (must be the group leader or an admin)
     */
    void deleteGroup(Long groupId, String username);
    
    /**
     * Get a group by ID
     * @param groupId the ID of the group to get
     * @return the group
     */
    GroupResponse getGroup(Long groupId);
    
    /**
     * Get all groups
     * @return a list of all groups
     */
    List<GroupResponse> getAllGroups();

    @Transactional(readOnly = true)
    @Cacheable(value = "pagedGroups", key = "{#pageable.pageNumber, #pageable.pageSize}")
    Page<GroupResponse> getAllGroups(Pageable pageable);

    /**
     * Get all groups led by a user
     * @param username the username of the leader
     * @return a list of groups led by the user
     */
    List<GroupResponse> getGroupsByLeader(String username);
    
    /**
     * Get all groups that a user is a member of
     * @param username the username of the member
     * @return a list of groups that the user is a member of
     */
    List<GroupResponse> getGroupsByMember(String username);
    
    /**
     * Add a user to a group
     * @param groupId the ID of the group
     * @param username the username of the user to add
     * @param requesterUsername the username of the user making the request (must be the group leader or an admin)
     * @return the updated group
     */
    GroupResponse addMember(Long groupId, String username, String requesterUsername);
    
    /**
     * Remove a user from a group
     * @param groupId the ID of the group
     * @param username the username of the user to remove
     * @param requesterUsername the username of the user making the request (must be the group leader, the user being removed, or an admin)
     * @return the updated group
     */
    GroupResponse removeMember(Long groupId, String username, String requesterUsername);
    
    /**
     * Join a group
     * @param groupId the ID of the group to join
     * @param username the username of the user joining the group
     * @return the updated group
     */
    GroupResponse joinGroup(Long groupId, String username);
    
    /**
     * Leave a group
     * @param groupId the ID of the group to leave
     * @param username the username of the user leaving the group
     * @return the updated group
     */
    GroupResponse leaveGroup(Long groupId, String username);
}