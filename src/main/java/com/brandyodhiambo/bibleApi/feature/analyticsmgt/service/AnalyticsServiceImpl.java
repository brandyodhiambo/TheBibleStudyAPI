package com.brandyodhiambo.bibleApi.feature.analyticsmgt.service;

import com.brandyodhiambo.bibleApi.exception.ResourceNotFoundException;
import com.brandyodhiambo.bibleApi.feature.analyticsmgt.models.ParticipationMetrics;
import com.brandyodhiambo.bibleApi.feature.analyticsmgt.models.ReadingProgress;
import com.brandyodhiambo.bibleApi.feature.analyticsmgt.models.dto.GroupAnalyticsResponse;
import com.brandyodhiambo.bibleApi.feature.analyticsmgt.models.dto.ParticipationMetricsResponse;
import com.brandyodhiambo.bibleApi.feature.analyticsmgt.models.dto.ReadingProgressResponse;
import com.brandyodhiambo.bibleApi.feature.analyticsmgt.repository.ParticipationMetricsRepository;
import com.brandyodhiambo.bibleApi.feature.analyticsmgt.repository.ReadingProgressRepository;
import com.brandyodhiambo.bibleApi.feature.eventmgt.models.RSVPStatus;
import com.brandyodhiambo.bibleApi.feature.eventmgt.repository.SessionRSVPRepository;
import com.brandyodhiambo.bibleApi.feature.eventmgt.repository.StudySessionRepository;
import com.brandyodhiambo.bibleApi.feature.groupmgt.models.Group;
import com.brandyodhiambo.bibleApi.feature.groupmgt.repository.GroupRepository;
import com.brandyodhiambo.bibleApi.feature.prayermgt.repository.PrayerRequestRepository;
import com.brandyodhiambo.bibleApi.feature.studymgt.models.ReadingPlan;
import com.brandyodhiambo.bibleApi.feature.studymgt.repository.CommentRepository;
import com.brandyodhiambo.bibleApi.feature.studymgt.repository.ReadingPlanRepository;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.Users;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.dto.UserSummary;
import com.brandyodhiambo.bibleApi.feature.usermgt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final ReadingProgressRepository readingProgressRepository;
    private final ParticipationMetricsRepository participationMetricsRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final ReadingPlanRepository readingPlanRepository;
    private final SessionRSVPRepository sessionRSVPRepository;
    private final StudySessionRepository studySessionRepository;
    private final PrayerRequestRepository prayerRequestRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ReadingProgressResponse trackReadingProgress(Long readingPlanId, String username, 
                                                     Integer completionPercentage, String lastCompletedReference) {
        ReadingPlan readingPlan = readingPlanRepository.findById(readingPlanId)
                .orElseThrow(() -> new ResourceNotFoundException("Reading Plan", "id", readingPlanId.toString()));

        Users user = userRepository.getUserByName(username);

        // Check if progress entry already exists
        Optional<ReadingProgress> existingProgressOpt = readingProgressRepository.findByReadingPlanAndUser(readingPlan, user);

        ReadingProgress progress;
        if (existingProgressOpt.isPresent()) {
            // Update existing progress
            progress = existingProgressOpt.get();
            progress.setCompletionPercentage(completionPercentage);
            progress.setLastCompletedReference(lastCompletedReference);
            progress.setLastActivityDate(LocalDate.now());

            // Check if completed
            if (completionPercentage >= 100) {
                progress.setCompleted(true);
            }
        } else {
            // Create new progress entry
            progress = ReadingProgress.builder()
                    .readingPlan(readingPlan)
                    .user(user)
                    .completionPercentage(completionPercentage)
                    .lastCompletedReference(lastCompletedReference)
                    .lastActivityDate(LocalDate.now())
                    .completed(completionPercentage >= 100)
                    .build();
        }

        ReadingProgress savedProgress = readingProgressRepository.save(progress);
        return mapToReadingProgressResponse(savedProgress);
    }

    @Override
    public ReadingProgressResponse getReadingProgress(Long readingPlanId, String username) {
        ReadingPlan readingPlan = readingPlanRepository.findById(readingPlanId)
                .orElseThrow(() -> new ResourceNotFoundException("Reading Plan", "id", readingPlanId.toString()));

        Users user = userRepository.getUserByName(username);

        ReadingProgress progress = readingProgressRepository.findByReadingPlanAndUser(readingPlan, user)
                .orElseThrow(() -> new ResourceNotFoundException("Reading Progress", "readingPlan and user", 
                        readingPlanId + " and " + username));

        return mapToReadingProgressResponse(progress);
    }

    @Override
    public List<ReadingProgressResponse> getUserReadingProgress(String username) {
        Users user = userRepository.getUserByName(username);

        return readingProgressRepository.findByUser(user).stream()
                .map(this::mapToReadingProgressResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReadingProgressResponse> getReadingPlanProgress(Long readingPlanId) {
        ReadingPlan readingPlan = readingPlanRepository.findById(readingPlanId)
                .orElseThrow(() -> new ResourceNotFoundException("Reading Plan", "id", readingPlanId.toString()));

        return readingProgressRepository.findByReadingPlan(readingPlan).stream()
                .map(this::mapToReadingProgressResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Double calculateAverageReadingPlanCompletion(Long readingPlanId) {
        ReadingPlan readingPlan = readingPlanRepository.findById(readingPlanId)
                .orElseThrow(() -> new ResourceNotFoundException("Reading Plan", "id", readingPlanId.toString()));

        return readingProgressRepository.calculateAverageCompletionPercentage(readingPlan);
    }

    // Helper methods for mapping entities to DTOs
    private ReadingProgressResponse mapToReadingProgressResponse(ReadingProgress progress) {
        return ReadingProgressResponse.builder()
                .id(progress.getId())
                .readingPlanId(progress.getReadingPlan().getId())
                .readingPlanTitle(progress.getReadingPlan().getTitle())
                .user(mapToUserSummary(progress.getUser()))
                .completionPercentage(progress.getCompletionPercentage())
                .lastCompletedReference(progress.getLastCompletedReference())
                .lastActivityDate(progress.getLastActivityDate())
                .completed(progress.isCompleted())
                .createdAt(progress.getCreatedAt())
                .updatedAt(progress.getUpdatedAt())
                .build();
    }

    private ParticipationMetricsResponse mapToParticipationMetricsResponse(ParticipationMetrics metrics) {
        return ParticipationMetricsResponse.builder()
                .id(metrics.getId())
                .groupId(metrics.getGroup().getId())
                .groupName(metrics.getGroup().getName())
                .user(mapToUserSummary(metrics.getUser()))
                .period(metrics.getPeriod())
                .sessionsAttended(metrics.getSessionsAttended())
                .totalSessions(metrics.getTotalSessions())
                .attendancePercentage(metrics.getAttendancePercentage())
                .chatMessagesSent(metrics.getChatMessagesSent())
                .prayerRequestsSubmitted(metrics.getPrayerRequestsSubmitted())
                .prayerRequestsAnswered(metrics.getPrayerRequestsAnswered())
                .studyCommentsMade(metrics.getStudyCommentsMade())
                .engagementScore(metrics.getEngagementScore())
                .lastUpdatedDate(metrics.getLastUpdatedDate())
                .createdAt(metrics.getCreatedAt())
                .updatedAt(metrics.getUpdatedAt())
                .build();
    }

    private UserSummary mapToUserSummary(Users user) {
        return new UserSummary(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail()
        );
    }

    // Helper method to calculate engagement score
    private int calculateEngagementScore(int sessionsAttended, int totalSessions, 
                                        int chatMessagesSent, int prayerRequestsSubmitted, 
                                        int prayerRequestsAnswered, int studyCommentsMade) {
        // Simple algorithm to calculate engagement score (0-100)
        // This can be refined based on specific requirements

        // Attendance component (40% of score)
        double attendanceScore = totalSessions > 0 
                ? (double) sessionsAttended / totalSessions * 40 
                : 0;

        // Chat activity component (20% of score)
        double chatScore = Math.min(chatMessagesSent, 10) * 2; // Cap at 20 points

        // Prayer request component (20% of score)
        double prayerScore = Math.min(prayerRequestsSubmitted + prayerRequestsAnswered, 10) * 2; // Cap at 20 points

        // Study comment component (20% of score)
        double commentScore = Math.min(studyCommentsMade, 10) * 2; // Cap at 20 points

        // Total score
        return (int) Math.round(attendanceScore + chatScore + prayerScore + commentScore);
    }

    // The remaining methods will be implemented in subsequent updates
    @Override
    @Transactional
    public ParticipationMetricsResponse updateParticipationMetrics(Long groupId, String username, YearMonth period) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId.toString()));

        Users user = userRepository.getUserByName(username);

        // Check if user is a member of the group
        if (!group.isMember(user)) {
            throw new IllegalArgumentException("User is not a member of this group");
        }

        // Get or create participation metrics
        Optional<ParticipationMetrics> existingMetricsOpt = participationMetricsRepository
                .findByGroupAndUserAndPeriodYearAndPeriodMonth(
                        group, user, period.getYear(), period.getMonthValue());

        ParticipationMetrics metrics;
        if (existingMetricsOpt.isPresent()) {
            metrics = existingMetricsOpt.get();
        } else {
            metrics = ParticipationMetrics.builder()
                    .group(group)
                    .user(user)
                    .periodYear(period.getYear())
                    .periodMonth(period.getMonthValue())
                    .sessionsAttended(0)
                    .totalSessions(0)
                    .chatMessagesSent(0)
                    .prayerRequestsSubmitted(0)
                    .prayerRequestsAnswered(0)
                    .studyCommentsMade(0)
                    .engagementScore(0)
                    .lastUpdatedDate(LocalDate.now())
                    .build();
        }

        // Calculate metrics for the period
        LocalDate startDate = period.atDay(1);
        LocalDate endDate = period.atEndOfMonth();

        // For a real implementation, we would need to add these methods to the repositories
        // For now, we'll use placeholder values for demonstration

        // Count sessions attended - placeholder
        int sessionsAttended = 5; // Example value

        // Count total sessions for the group in this period - placeholder
        int totalSessions = 8; // Example value

        // Count chat messages sent - placeholder
        int chatMessagesSent = 12; // Example value

        // Count prayer requests - placeholder
        int prayerRequestsSubmitted = 3; // Example value
        int prayerRequestsAnswered = 1; // Example value

        // Count study comments - placeholder
        int studyCommentsMade = 7; // Example value

        // In a real implementation, we would add custom query methods to the repositories:
        // 1. Add to SessionRSVPRepository:
        //    long countByUserAndStatusAndSessionDateBetween(Users user, RSVPStatus status, LocalDate start, LocalDate end);
        // 2. Add to StudySessionRepository:
        //    long countByGroupAndSessionDateBetween(Group group, LocalDate start, LocalDate end);
        // 3. Add to PrayerRequestRepository:
        //    long countByUserAndGroupAndCreatedAtBetween(Users user, Group group, LocalDateTime start, LocalDateTime end);
        //    long countByUserAndGroupAndAnsweredAndUpdatedAtBetween(Users user, Group group, boolean answered, LocalDateTime start, LocalDateTime end);
        // 4. Add to CommentRepository:
        //    long countByUserAndStudyMaterialGroupAndCreatedAtBetween(Users user, Group group, LocalDateTime start, LocalDateTime end);

        // Calculate engagement score
        int engagementScore = calculateEngagementScore(
                (int) sessionsAttended, (int) totalSessions, 
                (int) chatMessagesSent, (int) prayerRequestsSubmitted, 
                (int) prayerRequestsAnswered, (int) studyCommentsMade);

        // Update metrics
        metrics.setSessionsAttended((int) sessionsAttended);
        metrics.setTotalSessions((int) totalSessions);
        metrics.setChatMessagesSent((int) chatMessagesSent);
        metrics.setPrayerRequestsSubmitted((int) prayerRequestsSubmitted);
        metrics.setPrayerRequestsAnswered((int) prayerRequestsAnswered);
        metrics.setStudyCommentsMade((int) studyCommentsMade);
        metrics.setEngagementScore(engagementScore);
        metrics.setLastUpdatedDate(LocalDate.now());

        ParticipationMetrics savedMetrics = participationMetricsRepository.save(metrics);
        return mapToParticipationMetricsResponse(savedMetrics);
    }

    @Override
    public ParticipationMetricsResponse getParticipationMetrics(Long groupId, String username, YearMonth period) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId.toString()));

        Users user = userRepository.getUserByName(username);

        ParticipationMetrics metrics = participationMetricsRepository
                .findByGroupAndUserAndPeriodYearAndPeriodMonth(
                        group, user, period.getYear(), period.getMonthValue())
                .orElseThrow(() -> new ResourceNotFoundException("Participation Metrics", 
                        "group, user, and period", groupId + ", " + username + ", " + period));

        return mapToParticipationMetricsResponse(metrics);
    }

    @Override
    public List<ParticipationMetricsResponse> getUserParticipationMetrics(String username) {
        Users user = userRepository.getUserByName(username);

        return participationMetricsRepository.findByUser(user).stream()
                .map(this::mapToParticipationMetricsResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ParticipationMetricsResponse> getGroupParticipationMetrics(Long groupId, YearMonth period) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId.toString()));

        return participationMetricsRepository.findByGroupAndPeriodYearAndPeriodMonth(
                group, period.getYear(), period.getMonthValue()).stream()
                .map(this::mapToParticipationMetricsResponse)
                .collect(Collectors.toList());
    }

    @Override
    public GroupAnalyticsResponse getGroupAnalytics(Long groupId, YearMonth period) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId.toString()));

        // Get all metrics for this group in this period
        List<ParticipationMetrics> allMetrics = participationMetricsRepository
                .findByGroupAndPeriodYearAndPeriodMonth(group, period.getYear(), period.getMonthValue());

        // Get all reading plans for this group
        List<ReadingPlan> readingPlans = readingPlanRepository.findByGroup(group);

        // Calculate attendance metrics
        int totalMembers = group.getMembers().size();

        // In a real implementation, we would use actual data from the repositories
        // For now, we'll use placeholder values or calculate from the metrics we have

        // Calculate average attendance percentage
        Double averageAttendancePercentage = allMetrics.isEmpty() ? 0.0 :
                allMetrics.stream()
                        .mapToDouble(m -> m.getAttendancePercentage())
                        .average()
                        .orElse(0.0);

        // Find top attendees (up to 5)
        List<UserSummary> topAttendees = allMetrics.stream()
                .sorted(Comparator.comparing(ParticipationMetrics::getAttendancePercentage).reversed())
                .limit(5)
                .map(m -> mapToUserSummary(m.getUser()))
                .collect(Collectors.toList());

        // Calculate engagement metrics
        Double averageEngagementScore = allMetrics.isEmpty() ? 0.0 :
                allMetrics.stream()
                        .mapToDouble(m -> m.getEngagementScore())
                        .average()
                        .orElse(0.0);

        // Find most engaged members (up to 5)
        List<UserSummary> mostEngagedMembers = allMetrics.stream()
                .sorted(Comparator.comparing(ParticipationMetrics::getEngagementScore).reversed())
                .limit(5)
                .map(m -> mapToUserSummary(m.getUser()))
                .collect(Collectors.toList());

        // Calculate total chat messages and study comments
        int totalChatMessages = allMetrics.stream()
                .mapToInt(ParticipationMetrics::getChatMessagesSent)
                .sum();

        int totalStudyComments = allMetrics.stream()
                .mapToInt(ParticipationMetrics::getStudyCommentsMade)
                .sum();

        // Calculate prayer request metrics
        int totalPrayerRequestsSubmitted = allMetrics.stream()
                .mapToInt(ParticipationMetrics::getPrayerRequestsSubmitted)
                .sum();

        int totalPrayerRequestsAnswered = allMetrics.stream()
                .mapToInt(ParticipationMetrics::getPrayerRequestsAnswered)
                .sum();

        Double prayerAnsweredPercentage = totalPrayerRequestsSubmitted == 0 ? 0.0 :
                (double) totalPrayerRequestsAnswered / totalPrayerRequestsSubmitted * 100;

        // Calculate reading plan metrics
        int totalReadingPlans = readingPlans.size();

        // For each reading plan, get all progress entries and calculate average completion
        Double averageReadingPlanCompletion = 0.0;
        int completedReadingPlans = 0;
        List<UserSummary> topReaders = new ArrayList<>();

        if (!readingPlans.isEmpty()) {
            // Calculate average completion across all reading plans
            List<Double> planCompletions = new ArrayList<>();

            for (ReadingPlan plan : readingPlans) {
                Double avgCompletion = readingProgressRepository.calculateAverageCompletionPercentage(plan);
                if (avgCompletion != null) {
                    planCompletions.add(avgCompletion);
                }

                // In a real implementation, we would add a method to count completed plans:
                // long completed = readingProgressRepository.countByReadingPlanAndCompleted(plan, true);
                // For now, we'll use a placeholder value
                long completed = 2; // Example value
                completedReadingPlans += completed;
            }

            averageReadingPlanCompletion = planCompletions.isEmpty() ? 0.0 :
                    planCompletions.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

            // Find top readers (users with highest average completion percentage)
            // This would require a more complex query in a real implementation
            // For now, we'll leave it as an empty list
        }

        // Build and return the response
        return GroupAnalyticsResponse.builder()
                .groupId(group.getId())
                .groupName(group.getName())
                .period(period)
                .totalMembers(totalMembers)
                .totalSessions(allMetrics.isEmpty() ? 0 : allMetrics.get(0).getTotalSessions())
                .averageAttendancePercentage(averageAttendancePercentage)
                .topAttendees(topAttendees)
                .averageEngagementScore(averageEngagementScore)
                .mostEngagedMembers(mostEngagedMembers)
                .totalChatMessages(totalChatMessages)
                .totalStudyComments(totalStudyComments)
                .totalPrayerRequestsSubmitted(totalPrayerRequestsSubmitted)
                .totalPrayerRequestsAnswered(totalPrayerRequestsAnswered)
                .prayerAnsweredPercentage(prayerAnsweredPercentage)
                .totalReadingPlans(totalReadingPlans)
                .averageReadingPlanCompletion(averageReadingPlanCompletion)
                .completedReadingPlans(completedReadingPlans)
                .topReaders(topReaders)
                .build();
    }

    @Override
    public GroupAnalyticsResponse getGroupAnalyticsWithTrends(Long groupId, YearMonth period) {
        // Get current period analytics
        GroupAnalyticsResponse currentAnalytics = getGroupAnalytics(groupId, period);

        // Get previous period
        YearMonth previousPeriod = period.minusMonths(1);

        // Get previous period analytics
        GroupAnalyticsResponse previousAnalytics;
        try {
            previousAnalytics = getGroupAnalytics(groupId, previousPeriod);
        } catch (ResourceNotFoundException e) {
            // If no data for previous period, just return current analytics without trends
            return currentAnalytics;
        }

        // Calculate trends (percentage change from previous period)
        Double attendanceTrend = calculateTrend(
                previousAnalytics.getAverageAttendancePercentage(), 
                currentAnalytics.getAverageAttendancePercentage());

        Double engagementTrend = calculateTrend(
                previousAnalytics.getAverageEngagementScore(), 
                currentAnalytics.getAverageEngagementScore());

        Double prayerActivityTrend = calculateTrend(
                previousAnalytics.getTotalPrayerRequestsSubmitted(), 
                currentAnalytics.getTotalPrayerRequestsSubmitted());

        Double readingCompletionTrend = calculateTrend(
                previousAnalytics.getAverageReadingPlanCompletion(), 
                currentAnalytics.getAverageReadingPlanCompletion());

        // Add trends to the response
        return GroupAnalyticsResponse.builder()
                .groupId(currentAnalytics.getGroupId())
                .groupName(currentAnalytics.getGroupName())
                .period(currentAnalytics.getPeriod())
                .totalMembers(currentAnalytics.getTotalMembers())
                .totalSessions(currentAnalytics.getTotalSessions())
                .averageAttendancePercentage(currentAnalytics.getAverageAttendancePercentage())
                .topAttendees(currentAnalytics.getTopAttendees())
                .averageEngagementScore(currentAnalytics.getAverageEngagementScore())
                .mostEngagedMembers(currentAnalytics.getMostEngagedMembers())
                .totalChatMessages(currentAnalytics.getTotalChatMessages())
                .totalStudyComments(currentAnalytics.getTotalStudyComments())
                .totalPrayerRequestsSubmitted(currentAnalytics.getTotalPrayerRequestsSubmitted())
                .totalPrayerRequestsAnswered(currentAnalytics.getTotalPrayerRequestsAnswered())
                .prayerAnsweredPercentage(currentAnalytics.getPrayerAnsweredPercentage())
                .totalReadingPlans(currentAnalytics.getTotalReadingPlans())
                .averageReadingPlanCompletion(currentAnalytics.getAverageReadingPlanCompletion())
                .completedReadingPlans(currentAnalytics.getCompletedReadingPlans())
                .topReaders(currentAnalytics.getTopReaders())
                .attendanceTrend(attendanceTrend)
                .engagementTrend(engagementTrend)
                .prayerActivityTrend(prayerActivityTrend)
                .readingCompletionTrend(readingCompletionTrend)
                .build();
    }

    // Helper method to calculate trend (percentage change)
    private Double calculateTrend(Number previous, Number current) {
        if (previous == null || current == null || previous.doubleValue() == 0) {
            return 0.0;
        }

        return (current.doubleValue() - previous.doubleValue()) / previous.doubleValue() * 100;
    }

    @Override
    public List<ParticipationMetricsResponse> generateAttendanceReport(Long groupId, YearMonth period) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId.toString()));

        // Get all metrics for this group in this period
        List<ParticipationMetrics> metrics = participationMetricsRepository
                .findByGroupAndPeriodYearAndPeriodMonth(group, period.getYear(), period.getMonthValue());

        // Sort by attendance percentage (highest first)
        return metrics.stream()
                .sorted(Comparator.comparing(ParticipationMetrics::getAttendancePercentage).reversed())
                .map(this::mapToParticipationMetricsResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ParticipationMetricsResponse> generateEngagementReport(Long groupId, YearMonth period) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId.toString()));

        // Get all metrics for this group in this period
        List<ParticipationMetrics> metrics = participationMetricsRepository
                .findByGroupAndPeriodYearAndPeriodMonth(group, period.getYear(), period.getMonthValue());

        // Sort by engagement score (highest first)
        return metrics.stream()
                .sorted(Comparator.comparing(ParticipationMetrics::getEngagementScore).reversed())
                .map(this::mapToParticipationMetricsResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ParticipationMetricsResponse> generatePrayerActivityReport(Long groupId, YearMonth period) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId.toString()));

        // Get all metrics for this group in this period
        List<ParticipationMetrics> metrics = participationMetricsRepository
                .findByGroupAndPeriodYearAndPeriodMonth(group, period.getYear(), period.getMonthValue());

        // Sort by prayer request activity (submitted + answered, highest first)
        return metrics.stream()
                .sorted(Comparator.comparing(m -> m.getPrayerRequestsSubmitted() + m.getPrayerRequestsAnswered(), Comparator.reverseOrder()))
                .map(this::mapToParticipationMetricsResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReadingProgressResponse> generateReadingProgressReport(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId.toString()));

        // Get all reading plans for this group
        List<ReadingPlan> readingPlans = readingPlanRepository.findByGroup(group);

        // Get all reading progress entries for these plans
        List<ReadingProgress> allProgress = new ArrayList<>();
        for (ReadingPlan plan : readingPlans) {
            List<ReadingProgress> planProgress = readingProgressRepository.findByReadingPlan(plan);
            allProgress.addAll(planProgress);
        }

        // Sort by completion percentage (highest first)
        return allProgress.stream()
                .sorted(Comparator.comparing(ReadingProgress::getCompletionPercentage).reversed())
                .map(this::mapToReadingProgressResponse)
                .collect(Collectors.toList());
    }
}
