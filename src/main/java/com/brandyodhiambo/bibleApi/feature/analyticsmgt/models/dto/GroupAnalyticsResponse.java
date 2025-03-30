package com.brandyodhiambo.bibleApi.feature.analyticsmgt.models.dto;

import com.brandyodhiambo.bibleApi.feature.usermgt.models.dto.UserSummary;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.YearMonth;
import java.util.List;

/**
 * DTO for group analytics response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupAnalyticsResponse {
    private Long groupId;
    private String groupName;
    private YearMonth period;
    
    // Attendance metrics
    private Integer totalMembers;
    private Integer totalSessions;
    private Double averageAttendancePercentage;
    private List<UserSummary> topAttendees;
    
    // Engagement metrics
    private Double averageEngagementScore;
    private List<UserSummary> mostEngagedMembers;
    private Integer totalChatMessages;
    private Integer totalStudyComments;
    
    // Prayer request metrics
    private Integer totalPrayerRequestsSubmitted;
    private Integer totalPrayerRequestsAnswered;
    private Double prayerAnsweredPercentage;
    
    // Reading plan metrics
    private Integer totalReadingPlans;
    private Double averageReadingPlanCompletion;
    private Integer completedReadingPlans;
    private List<UserSummary> topReaders;
    
    // Trend indicators (compared to previous period)
    private Double attendanceTrend; // Positive value means improvement
    private Double engagementTrend;
    private Double prayerActivityTrend;
    private Double readingCompletionTrend;
}