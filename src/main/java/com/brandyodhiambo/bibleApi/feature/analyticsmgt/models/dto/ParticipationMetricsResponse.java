package com.brandyodhiambo.bibleApi.feature.analyticsmgt.models.dto;

import com.brandyodhiambo.bibleApi.feature.usermgt.models.dto.UserSummary;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;

/**
 * DTO for participation metrics response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationMetricsResponse {
    private Long id;
    private Long groupId;
    private String groupName;
    private UserSummary user;
    private YearMonth period;
    private Integer sessionsAttended;
    private Integer totalSessions;
    private Integer attendancePercentage;
    private Integer chatMessagesSent;
    private Integer prayerRequestsSubmitted;
    private Integer prayerRequestsAnswered;
    private Integer studyCommentsMade;
    private Integer engagementScore;
    private LocalDate lastUpdatedDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}