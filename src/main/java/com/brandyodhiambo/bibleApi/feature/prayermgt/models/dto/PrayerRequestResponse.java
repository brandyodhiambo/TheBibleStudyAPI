package com.brandyodhiambo.bibleApi.feature.prayermgt.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrayerRequestResponse {
    private Long id;
    private String title;
    private String description;
    private Long groupId;
    private String groupName;
    private Long userId;
    private String username;
    private boolean answered;
    private String testimony;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}