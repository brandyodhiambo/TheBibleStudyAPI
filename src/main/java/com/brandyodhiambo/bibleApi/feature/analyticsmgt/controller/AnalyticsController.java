package com.brandyodhiambo.bibleApi.feature.analyticsmgt.controller;

import com.brandyodhiambo.bibleApi.feature.analyticsmgt.models.dto.GroupAnalyticsResponse;
import com.brandyodhiambo.bibleApi.feature.analyticsmgt.models.dto.ParticipationMetricsResponse;
import com.brandyodhiambo.bibleApi.feature.analyticsmgt.models.dto.ReadingProgressResponse;
import com.brandyodhiambo.bibleApi.feature.analyticsmgt.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

/**
 * Controller for analytics and reporting endpoints
 */
@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    /**
     * Track user's reading plan progress
     */
    @PostMapping("/reading-plans/{readingPlanId}/progress")
    public ResponseEntity<ReadingProgressResponse> trackReadingProgress(
            @PathVariable Long readingPlanId,
            @RequestParam Integer completionPercentage,
            @RequestParam(required = false) String lastCompletedReference) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        ReadingProgressResponse response = analyticsService.trackReadingProgress(
                readingPlanId, username, completionPercentage, lastCompletedReference);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get reading progress for a specific user and reading plan
     */
    @GetMapping("/reading-plans/{readingPlanId}/progress")
    public ResponseEntity<ReadingProgressResponse> getReadingProgress(
            @PathVariable Long readingPlanId) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        ReadingProgressResponse response = analyticsService.getReadingProgress(readingPlanId, username);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get all reading progress entries for the current user
     */
    @GetMapping("/reading-plans/progress")
    public ResponseEntity<List<ReadingProgressResponse>> getUserReadingProgress() {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        List<ReadingProgressResponse> response = analyticsService.getUserReadingProgress(username);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get all reading progress entries for a specific reading plan
     * Only accessible by group leaders and admins
     */
    @GetMapping("/reading-plans/{readingPlanId}/all-progress")
    @PreAuthorize("hasRole('LEADER') or hasRole('ADMIN')")
    public ResponseEntity<List<ReadingProgressResponse>> getReadingPlanProgress(
            @PathVariable Long readingPlanId) {
        
        List<ReadingProgressResponse> response = analyticsService.getReadingPlanProgress(readingPlanId);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Calculate average completion percentage for a reading plan
     * Only accessible by group leaders and admins
     */
    @GetMapping("/reading-plans/{readingPlanId}/average-completion")
    @PreAuthorize("hasRole('LEADER') or hasRole('ADMIN')")
    public ResponseEntity<Double> calculateAverageReadingPlanCompletion(
            @PathVariable Long readingPlanId) {
        
        Double averageCompletion = analyticsService.calculateAverageReadingPlanCompletion(readingPlanId);
        
        return ResponseEntity.ok(averageCompletion);
    }

    /**
     * Update participation metrics for the current user in a group
     * This endpoint would typically be called by a scheduled job, but can also be triggered manually
     */
    @PostMapping("/groups/{groupId}/participation")
    public ResponseEntity<ParticipationMetricsResponse> updateParticipationMetrics(
            @PathVariable Long groupId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth period) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        ParticipationMetricsResponse response = analyticsService.updateParticipationMetrics(
                groupId, username, period);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get participation metrics for the current user in a group for a specific period
     */
    @GetMapping("/groups/{groupId}/participation")
    public ResponseEntity<ParticipationMetricsResponse> getParticipationMetrics(
            @PathVariable Long groupId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth period) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        ParticipationMetricsResponse response = analyticsService.getParticipationMetrics(
                groupId, username, period);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get all participation metrics for the current user
     */
    @GetMapping("/participation")
    public ResponseEntity<List<ParticipationMetricsResponse>> getUserParticipationMetrics() {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        List<ParticipationMetricsResponse> response = analyticsService.getUserParticipationMetrics(username);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get all participation metrics for a group in a specific period
     * Only accessible by group leaders and admins
     */
    @GetMapping("/groups/{groupId}/all-participation")
    @PreAuthorize("hasRole('LEADER') or hasRole('ADMIN')")
    public ResponseEntity<List<ParticipationMetricsResponse>> getGroupParticipationMetrics(
            @PathVariable Long groupId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth period) {
        
        List<ParticipationMetricsResponse> response = analyticsService.getGroupParticipationMetrics(
                groupId, period);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get aggregated analytics for a group in a specific period
     * Only accessible by group leaders and admins
     */
    @GetMapping("/groups/{groupId}/analytics")
    @PreAuthorize("hasRole('LEADER') or hasRole('ADMIN')")
    public ResponseEntity<GroupAnalyticsResponse> getGroupAnalytics(
            @PathVariable Long groupId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth period) {
        
        GroupAnalyticsResponse response = analyticsService.getGroupAnalytics(groupId, period);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get aggregated analytics for a group with trend comparison to previous period
     * Only accessible by group leaders and admins
     */
    @GetMapping("/groups/{groupId}/analytics/trends")
    @PreAuthorize("hasRole('LEADER') or hasRole('ADMIN')")
    public ResponseEntity<GroupAnalyticsResponse> getGroupAnalyticsWithTrends(
            @PathVariable Long groupId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth period) {
        
        GroupAnalyticsResponse response = analyticsService.getGroupAnalyticsWithTrends(groupId, period);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Generate a report of member attendance for a group in a specific period
     * Only accessible by group leaders and admins
     */
    @GetMapping("/groups/{groupId}/reports/attendance")
    @PreAuthorize("hasRole('LEADER') or hasRole('ADMIN')")
    public ResponseEntity<List<ParticipationMetricsResponse>> generateAttendanceReport(
            @PathVariable Long groupId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth period) {
        
        List<ParticipationMetricsResponse> response = analyticsService.generateAttendanceReport(
                groupId, period);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Generate a report of member engagement for a group in a specific period
     * Only accessible by group leaders and admins
     */
    @GetMapping("/groups/{groupId}/reports/engagement")
    @PreAuthorize("hasRole('LEADER') or hasRole('ADMIN')")
    public ResponseEntity<List<ParticipationMetricsResponse>> generateEngagementReport(
            @PathVariable Long groupId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth period) {
        
        List<ParticipationMetricsResponse> response = analyticsService.generateEngagementReport(
                groupId, period);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Generate a report of prayer request activity for a group in a specific period
     * Only accessible by group leaders and admins
     */
    @GetMapping("/groups/{groupId}/reports/prayer-activity")
    @PreAuthorize("hasRole('LEADER') or hasRole('ADMIN')")
    public ResponseEntity<List<ParticipationMetricsResponse>> generatePrayerActivityReport(
            @PathVariable Long groupId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth period) {
        
        List<ParticipationMetricsResponse> response = analyticsService.generatePrayerActivityReport(
                groupId, period);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Generate a report of reading plan progress for a group
     * Only accessible by group leaders and admins
     */
    @GetMapping("/groups/{groupId}/reports/reading-progress")
    @PreAuthorize("hasRole('LEADER') or hasRole('ADMIN')")
    public ResponseEntity<List<ReadingProgressResponse>> generateReadingProgressReport(
            @PathVariable Long groupId) {
        
        List<ReadingProgressResponse> response = analyticsService.generateReadingProgressReport(groupId);
        
        return ResponseEntity.ok(response);
    }
}