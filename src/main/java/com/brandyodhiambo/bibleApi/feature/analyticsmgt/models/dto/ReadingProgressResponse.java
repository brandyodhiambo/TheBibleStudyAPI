package com.brandyodhiambo.bibleApi.feature.analyticsmgt.models.dto;

import com.brandyodhiambo.bibleApi.feature.usermgt.models.dto.UserSummary;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for reading progress response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadingProgressResponse {
    private Long id;
    private Long readingPlanId;
    private String readingPlanTitle;
    private UserSummary user;
    private Integer completionPercentage;
    private String lastCompletedReference;
    private LocalDate lastActivityDate;
    private boolean completed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}