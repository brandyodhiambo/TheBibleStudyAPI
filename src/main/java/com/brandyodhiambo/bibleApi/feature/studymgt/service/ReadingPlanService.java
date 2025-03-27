package com.brandyodhiambo.bibleApi.feature.studymgt.service;

import com.brandyodhiambo.bibleApi.feature.studymgt.models.dto.ReadingPlanResponse;

import java.time.LocalDate;
import java.util.List;

public interface ReadingPlanService {
    
    /**
     * Create a new reading plan
     * 
     * @param groupId The ID of the group to assign the reading plan to
     * @param title The title of the reading plan
     * @param description The description of the reading plan
     * @param bibleReferences The Bible references to be read
     * @param startDate The date when the reading plan starts
     * @param endDate The date when the reading plan ends
     * @param topics The topics or keywords for searching the reading plan
     * @param username The username of the user creating the reading plan
     * @return The created reading plan
     */
    ReadingPlanResponse createReadingPlan(Long groupId, String title, String description, 
                                         String bibleReferences, LocalDate startDate, 
                                         LocalDate endDate, String topics, String username);
    
    /**
     * Get a reading plan by ID
     * 
     * @param planId The ID of the reading plan
     * @return The reading plan
     */
    ReadingPlanResponse getReadingPlan(Long planId);
    
    /**
     * Get all reading plans for a group
     * 
     * @param groupId The ID of the group
     * @return List of reading plans
     */
    List<ReadingPlanResponse> getReadingPlansByGroup(Long groupId);
    
    /**
     * Get all reading plans created by a user
     * 
     * @param username The username of the user
     * @return List of reading plans
     */
    List<ReadingPlanResponse> getReadingPlansByUser(String username);
    
    /**
     * Search reading plans by a search term
     * 
     * @param searchTerm The search term
     * @return List of matching reading plans
     */
    List<ReadingPlanResponse> searchReadingPlans(String searchTerm);
    
    /**
     * Search reading plans by a search term within a group
     * 
     * @param searchTerm The search term
     * @param groupId The ID of the group
     * @return List of matching reading plans
     */
    List<ReadingPlanResponse> searchReadingPlansInGroup(String searchTerm, Long groupId);
    
    /**
     * Get reading plans within a date range
     * 
     * @param startDate The start date
     * @param endDate The end date
     * @return List of reading plans
     */
    List<ReadingPlanResponse> getReadingPlansByDateRange(LocalDate startDate, LocalDate endDate);
    
    /**
     * Get reading plans within a date range for a group
     * 
     * @param groupId The ID of the group
     * @param startDate The start date
     * @param endDate The end date
     * @return List of reading plans
     */
    List<ReadingPlanResponse> getReadingPlansByGroupAndDateRange(Long groupId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Update a reading plan
     * 
     * @param planId The ID of the reading plan to update
     * @param title The new title (or null to keep the existing one)
     * @param description The new description (or null to keep the existing one)
     * @param bibleReferences The new Bible references (or null to keep the existing ones)
     * @param startDate The new start date (or null to keep the existing one)
     * @param endDate The new end date (or null to keep the existing one)
     * @param topics The new topics (or null to keep the existing ones)
     * @param username The username of the user updating the reading plan
     * @return The updated reading plan
     */
    ReadingPlanResponse updateReadingPlan(Long planId, String title, String description, 
                                         String bibleReferences, LocalDate startDate, 
                                         LocalDate endDate, String topics, String username);
    
    /**
     * Delete a reading plan
     * 
     * @param planId The ID of the reading plan to delete
     * @param username The username of the user deleting the reading plan
     */
    void deleteReadingPlan(Long planId, String username);
}