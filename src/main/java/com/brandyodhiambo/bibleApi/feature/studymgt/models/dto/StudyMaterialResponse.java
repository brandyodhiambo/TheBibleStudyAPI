package com.brandyodhiambo.bibleApi.feature.studymgt.models.dto;

import com.brandyodhiambo.bibleApi.feature.usermgt.models.dto.UserSummary;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyMaterialResponse {
    private Long id;
    private String title;
    private String description;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private Long groupId;
    private String groupName;
    private UserSummary uploadedBy;
    private String keywords;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}