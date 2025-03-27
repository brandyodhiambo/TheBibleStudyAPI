package com.brandyodhiambo.bibleApi.feature.studymgt.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse {
    private Long id;
    private String content;
    private Long studyMaterialId;
    private String studyMaterialTitle;
    private Long userId;
    private String username;
    private LocalDateTime createdAt;
}