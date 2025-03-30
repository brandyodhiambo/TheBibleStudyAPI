package com.brandyodhiambo.bibleApi.feature.analyticsmgt.service;

import com.brandyodhiambo.bibleApi.feature.analyticsmgt.models.dto.GroupAnalyticsResponse;
import com.brandyodhiambo.bibleApi.feature.analyticsmgt.models.dto.ParticipationMetricsResponse;
import com.brandyodhiambo.bibleApi.feature.analyticsmgt.models.dto.ReadingProgressResponse;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

/**
 * Service interface for analytics and reporting
 */
public interface AnalyticsService {

    /**
     * Track user's reading plan progress
     * @param readingPlanId ID of the reading plan
     * @param username Username of the user
     * @param completionPercentage Percentage of completion (0-100)
     * @param lastCompletedReference Last completed reference in the reading plan
     * @return Updated reading progress
     */
    ReadingProgressResponse trackReadingProgress(Long readingPlanId, String username, 
                                              Integer completionPercentage, String lastCompletedReference);

    /**
     * Get reading progress for a specific user and reading plan
     * @param readingPlanId ID of the reading plan
     * @param username Username of the user
     * @return Reading progress
     */
    ReadingProgressResponse getReadingProgress(Long readingPlanId, String username);

    /**
     * Get all reading progress entries for a specific user
     * @param username Username of the user
     * @return List of reading progress entries
     */
    List<ReadingProgressResponse> getUserReadingProgress(String username);

    /**
     * Get all reading progress entries for a specific reading plan
     * @param readingPlanId ID of the reading plan
     * @return List of reading progress entries
     */
    List<ReadingProgressResponse> getReadingPlanProgress(Long readingPlanId);

    /**
     * Calculate average completion percentage for a reading plan
     * @param readingPlanId ID of the reading plan
     * @return Average completion percentage
     */
    Double calculateAverageReadingPlanCompletion(Long readingPlanId);

    /**
     * Update participation metrics for a user in a group
     * This method should be called periodically to update metrics
     * @param groupId ID of the group
     * @param username Username of the user
     * @param period Year and month for which to update metrics
     * @return Updated participation metrics
     */
    ParticipationMetricsResponse updateParticipationMetrics(Long groupId, String username, YearMonth period);

    /**
     * Get participation metrics for a specific user in a specific group for a specific period
     * @param groupId ID of the group
     * @param username Username of the user
     * @param period Year and month
     * @return Participation metrics
     */
    ParticipationMetricsResponse getParticipationMetrics(Long groupId, String username, YearMonth period);

    /**
     * Get all participation metrics for a specific user
     * @param username Username of the user
     * @return List of participation metrics
     */
    List<ParticipationMetricsResponse> getUserParticipationMetrics(String username);

    /**
     * Get all participation metrics for a specific group in a specific period
     * @param groupId ID of the group
     * @param period Year and month
     * @return List of participation metrics
     */
    List<ParticipationMetricsResponse> getGroupParticipationMetrics(Long groupId, YearMonth period);

    /**
     * Get aggregated analytics for a group in a specific period
     * @param groupId ID of the group
     * @param period Year and month
     * @return Group analytics
     */
    GroupAnalyticsResponse getGroupAnalytics(Long groupId, YearMonth period);

    /**
     * Get aggregated analytics for a group with trend comparison to previous period
     * @param groupId ID of the group
     * @param period Year and month
     * @return Group analytics with trend indicators
     */
    GroupAnalyticsResponse getGroupAnalyticsWithTrends(Long groupId, YearMonth period);

    /**
     * Generate a report of member attendance for a group in a specific period
     * @param groupId ID of the group
     * @param period Year and month
     * @return List of participation metrics sorted by attendance percentage
     */
    List<ParticipationMetricsResponse> generateAttendanceReport(Long groupId, YearMonth period);

    /**
     * Generate a report of member engagement for a group in a specific period
     * @param groupId ID of the group
     * @param period Year and month
     * @return List of participation metrics sorted by engagement score
     */
    List<ParticipationMetricsResponse> generateEngagementReport(Long groupId, YearMonth period);

    /**
     * Generate a report of prayer request activity for a group in a specific period
     * @param groupId ID of the group
     * @param period Year and month
     * @return List of participation metrics sorted by prayer request activity
     */
    List<ParticipationMetricsResponse> generatePrayerActivityReport(Long groupId, YearMonth period);

    /**
     * Generate a report of reading plan progress for a group
     * @param groupId ID of the group
     * @return List of reading progress entries for all reading plans in the group
     */
    List<ReadingProgressResponse> generateReadingProgressReport(Long groupId);
}