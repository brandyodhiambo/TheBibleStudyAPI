package com.brandyodhiambo.bibleApi.feature.studymgt.service;

import com.brandyodhiambo.bibleApi.exception.ResourceNotFoundException;
import com.brandyodhiambo.bibleApi.feature.groupmgt.models.Group;
import com.brandyodhiambo.bibleApi.feature.groupmgt.repository.GroupRepository;
import com.brandyodhiambo.bibleApi.feature.studymgt.models.StudyMaterial;
import com.brandyodhiambo.bibleApi.feature.studymgt.models.dto.StudyMaterialResponse;
import com.brandyodhiambo.bibleApi.feature.studymgt.repository.StudyMaterialRepository;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.RoleName;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.Users;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.dto.UserSummary;
import com.brandyodhiambo.bibleApi.feature.usermgt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyMaterialServiceImpl implements StudyMaterialService {

    private final StudyMaterialRepository studyMaterialRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public StudyMaterialResponse uploadStudyMaterial(Long groupId, String title, String description,
                                                   String keywords, MultipartFile file, String username) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId.toString()));
        
        Users user = userRepository.getUserByName(username);
        
        // Check if the user is the group leader or an admin
        boolean isAdmin = user.getRole().stream()
                .anyMatch(role -> role.getName() == RoleName.ROLE_ADMIN);
        boolean isLeader = group.isLeader(user);
        
        if (!isLeader && !isAdmin) {
            throw new AccessDeniedException("Only the group leader or an admin can upload study materials");
        }
        
        try {
            StudyMaterial material = StudyMaterial.builder()
                    .title(title)
                    .description(description)
                    .fileName(file.getOriginalFilename())
                    .fileType(file.getContentType())
                    .fileSize(file.getSize())
                    .fileData(file.getBytes())
                    .group(group)
                    .uploadedBy(user)
                    .keywords(keywords)
                    .build();
            
            StudyMaterial savedMaterial = studyMaterialRepository.save(material);
            
            return mapToStudyMaterialResponse(savedMaterial);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload study material", e);
        }
    }

    @Override
    public StudyMaterialResponse getStudyMaterial(Long materialId) {
        StudyMaterial material = studyMaterialRepository.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException("Study Material", "id", materialId.toString()));
        
        return mapToStudyMaterialResponse(material);
    }

    @Override
    public List<StudyMaterialResponse> getStudyMaterialsByGroup(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId.toString()));
        
        return studyMaterialRepository.findByGroup(group).stream()
                .map(this::mapToStudyMaterialResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<StudyMaterialResponse> getStudyMaterialsByUser(String username) {
        Users user = userRepository.getUserByName(username);
        
        return studyMaterialRepository.findByUploadedBy(user).stream()
                .map(this::mapToStudyMaterialResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<StudyMaterialResponse> searchStudyMaterials(String searchTerm) {
        return studyMaterialRepository.searchByTerm(searchTerm).stream()
                .map(this::mapToStudyMaterialResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<StudyMaterialResponse> searchStudyMaterialsInGroup(String searchTerm, Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId.toString()));
        
        return studyMaterialRepository.searchByTermInGroup(searchTerm, group).stream()
                .map(this::mapToStudyMaterialResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public StudyMaterialResponse updateStudyMaterial(Long materialId, String title, String description,
                                                   String keywords, String username) {
        StudyMaterial material = studyMaterialRepository.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException("Study Material", "id", materialId.toString()));
        
        Users user = userRepository.getUserByName(username);
        
        // Check if the user is the uploader, the group leader, or an admin
        boolean isAdmin = user.getRole().stream()
                .anyMatch(role -> role.getName() == RoleName.ROLE_ADMIN);
        boolean isUploader = material.getUploadedBy().equals(user);
        boolean isLeader = material.getGroup().isLeader(user);
        
        if (!isUploader && !isLeader && !isAdmin) {
            throw new AccessDeniedException("Only the uploader, the group leader, or an admin can update study materials");
        }
        
        // Update the material fields if provided
        if (title != null) {
            material.setTitle(title);
        }
        
        if (description != null) {
            material.setDescription(description);
        }
        
        if (keywords != null) {
            material.setKeywords(keywords);
        }
        
        StudyMaterial updatedMaterial = studyMaterialRepository.save(material);
        
        return mapToStudyMaterialResponse(updatedMaterial);
    }

    @Override
    @Transactional
    public void deleteStudyMaterial(Long materialId, String username) {
        StudyMaterial material = studyMaterialRepository.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException("Study Material", "id", materialId.toString()));
        
        Users user = userRepository.getUserByName(username);
        
        // Check if the user is the uploader, the group leader, or an admin
        boolean isAdmin = user.getRole().stream()
                .anyMatch(role -> role.getName() == RoleName.ROLE_ADMIN);
        boolean isUploader = material.getUploadedBy().equals(user);
        boolean isLeader = material.getGroup().isLeader(user);
        
        if (!isUploader && !isLeader && !isAdmin) {
            throw new AccessDeniedException("Only the uploader, the group leader, or an admin can delete study materials");
        }
        
        studyMaterialRepository.delete(material);
    }

    @Override
    public StudyMaterial downloadStudyMaterial(Long materialId) {
        return studyMaterialRepository.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException("Study Material", "id", materialId.toString()));
    }
    
    // Helper method to map StudyMaterial entity to StudyMaterialResponse DTO
    private StudyMaterialResponse mapToStudyMaterialResponse(StudyMaterial material) {
        return StudyMaterialResponse.builder()
                .id(material.getId())
                .title(material.getTitle())
                .description(material.getDescription())
                .fileName(material.getFileName())
                .fileType(material.getFileType())
                .fileSize(material.getFileSize())
                .groupId(material.getGroup().getId())
                .groupName(material.getGroup().getName())
                .uploadedBy(mapToUserSummary(material.getUploadedBy()))
                .keywords(material.getKeywords())
                .createdAt(material.getCreatedAt())
                .updatedAt(material.getUpdatedAt())
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