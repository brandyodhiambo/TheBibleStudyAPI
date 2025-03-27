package com.brandyodhiambo.bibleApi.feature.studymgt.controller;

import com.brandyodhiambo.bibleApi.feature.studymgt.models.StudyMaterial;
import com.brandyodhiambo.bibleApi.feature.studymgt.models.dto.StudyMaterialResponse;
import com.brandyodhiambo.bibleApi.feature.studymgt.service.StudyMaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/study-materials")
@RequiredArgsConstructor
public class StudyMaterialController {

    private final StudyMaterialService studyMaterialService;

    @PostMapping("/upload")
    @PreAuthorize("hasRole('LEADER') or hasRole('ADMIN')")
    public ResponseEntity<StudyMaterialResponse> uploadStudyMaterial(
            @RequestParam("groupId") Long groupId,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("keywords") String keywords,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        
        String username = authentication.getName();
        StudyMaterialResponse response = studyMaterialService.uploadStudyMaterial(
                groupId, title, description, keywords, file, username);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{materialId}")
    public ResponseEntity<StudyMaterialResponse> getStudyMaterial(@PathVariable Long materialId) {
        StudyMaterialResponse response = studyMaterialService.getStudyMaterial(materialId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<StudyMaterialResponse>> getStudyMaterialsByGroup(@PathVariable Long groupId) {
        List<StudyMaterialResponse> response = studyMaterialService.getStudyMaterialsByGroup(groupId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user")
    public ResponseEntity<List<StudyMaterialResponse>> getStudyMaterialsByUser(Authentication authentication) {
        String username = authentication.getName();
        List<StudyMaterialResponse> response = studyMaterialService.getStudyMaterialsByUser(username);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<List<StudyMaterialResponse>> searchStudyMaterials(@RequestParam("term") String searchTerm) {
        List<StudyMaterialResponse> response = studyMaterialService.searchStudyMaterials(searchTerm);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/group/{groupId}")
    public ResponseEntity<List<StudyMaterialResponse>> searchStudyMaterialsInGroup(
            @RequestParam("term") String searchTerm,
            @PathVariable Long groupId) {
        List<StudyMaterialResponse> response = studyMaterialService.searchStudyMaterialsInGroup(searchTerm, groupId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{materialId}")
    public ResponseEntity<StudyMaterialResponse> updateStudyMaterial(
            @PathVariable Long materialId,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "keywords", required = false) String keywords,
            Authentication authentication) {
        
        String username = authentication.getName();
        StudyMaterialResponse response = studyMaterialService.updateStudyMaterial(
                materialId, title, description, keywords, username);
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{materialId}")
    public ResponseEntity<Void> deleteStudyMaterial(
            @PathVariable Long materialId,
            Authentication authentication) {
        
        String username = authentication.getName();
        studyMaterialService.deleteStudyMaterial(materialId, username);
        
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/download/{materialId}")
    public ResponseEntity<Resource> downloadStudyMaterial(@PathVariable Long materialId) {
        StudyMaterial material = studyMaterialService.downloadStudyMaterial(materialId);
        
        ByteArrayResource resource = new ByteArrayResource(material.getFileData());
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + material.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(material.getFileType()))
                .contentLength(material.getFileSize())
                .body(resource);
    }
}