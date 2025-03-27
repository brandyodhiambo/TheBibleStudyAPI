package com.brandyodhiambo.bibleApi.feature.studymgt.service;

import com.brandyodhiambo.bibleApi.feature.studymgt.models.StudyMaterial;
import com.brandyodhiambo.bibleApi.feature.studymgt.models.dto.StudyMaterialResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StudyMaterialService {
    
    /**
     * Upload a new study material
     * 
     * @param groupId The ID of the group to upload the material to
     * @param title The title of the study material
     * @param description The description of the study material
     * @param keywords The keywords for searching the study material
     * @param file The file to upload
     * @param username The username of the user uploading the material
     * @return The uploaded study material
     */
    StudyMaterialResponse uploadStudyMaterial(Long groupId, String title, String description, 
                                             String keywords, MultipartFile file, String username);
    
    /**
     * Get a study material by ID
     * 
     * @param materialId The ID of the study material
     * @return The study material
     */
    StudyMaterialResponse getStudyMaterial(Long materialId);
    
    /**
     * Get all study materials for a group
     * 
     * @param groupId The ID of the group
     * @return List of study materials
     */
    List<StudyMaterialResponse> getStudyMaterialsByGroup(Long groupId);
    
    /**
     * Get all study materials uploaded by a user
     * 
     * @param username The username of the user
     * @return List of study materials
     */
    List<StudyMaterialResponse> getStudyMaterialsByUser(String username);
    
    /**
     * Search study materials by a search term
     * 
     * @param searchTerm The search term
     * @return List of matching study materials
     */
    List<StudyMaterialResponse> searchStudyMaterials(String searchTerm);
    
    /**
     * Search study materials by a search term within a group
     * 
     * @param searchTerm The search term
     * @param groupId The ID of the group
     * @return List of matching study materials
     */
    List<StudyMaterialResponse> searchStudyMaterialsInGroup(String searchTerm, Long groupId);
    
    /**
     * Update a study material
     * 
     * @param materialId The ID of the study material to update
     * @param title The new title (or null to keep the existing one)
     * @param description The new description (or null to keep the existing one)
     * @param keywords The new keywords (or null to keep the existing one)
     * @param username The username of the user updating the material
     * @return The updated study material
     */
    StudyMaterialResponse updateStudyMaterial(Long materialId, String title, String description, 
                                             String keywords, String username);
    
    /**
     * Delete a study material
     * 
     * @param materialId The ID of the study material to delete
     * @param username The username of the user deleting the material
     */
    void deleteStudyMaterial(Long materialId, String username);
    
    /**
     * Download a study material file
     * 
     * @param materialId The ID of the study material
     * @return The study material entity with file data
     */
    StudyMaterial downloadStudyMaterial(Long materialId);
}