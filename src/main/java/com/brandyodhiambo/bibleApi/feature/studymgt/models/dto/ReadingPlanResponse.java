package com.brandyodhiambo.bibleApi.feature.studymgt.models.dto;

import com.brandyodhiambo.bibleApi.feature.usermgt.models.dto.UserSummary;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReadingPlanResponse {
    private Long id;
    private String title;
    private String description;
    private String bibleReferences;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long groupId;
    private String groupName;
    private UserSummary createdBy;
    private String topics;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}