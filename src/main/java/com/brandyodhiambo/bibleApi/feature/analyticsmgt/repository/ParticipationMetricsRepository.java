package com.brandyodhiambo.bibleApi.feature.analyticsmgt.repository;

import com.brandyodhiambo.bibleApi.feature.analyticsmgt.models.ParticipationMetrics;
import com.brandyodhiambo.bibleApi.feature.groupmgt.models.Group;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipationMetricsRepository extends JpaRepository<ParticipationMetrics, Long> {

    /**
     * Find participation metrics for a specific user in a specific group for a specific period
     */
    Optional<ParticipationMetrics> findByGroupAndUserAndPeriodYearAndPeriodMonth(
            Group group, Users user, Integer periodYear, Integer periodMonth);

    /**
     * Find all participation metrics for a specific user
     */
    List<ParticipationMetrics> findByUser(Users user);

    /**
     * Find all participation metrics for a specific group
     */
    List<ParticipationMetrics> findByGroup(Group group);

    /**
     * Find all participation metrics for a specific group in a specific period
     */
    List<ParticipationMetrics> findByGroupAndPeriodYearAndPeriodMonth(
            Group group, Integer periodYear, Integer periodMonth);

    /**
     * Find all participation metrics for a specific user in a specific period
     */
    List<ParticipationMetrics> findByUserAndPeriodYearAndPeriodMonth(
            Users user, Integer periodYear, Integer periodMonth);

    /**
     * Find all participation metrics for a specific user in a specific group
     */
    List<ParticipationMetrics> findByGroupAndUser(Group group, Users user);

    /**
     * Find all participation metrics updated after a specific date
     */
    List<ParticipationMetrics> findByLastUpdatedDateAfter(LocalDate date);

    /**
     * Calculate average attendance percentage for a group in a specific period
     */
    @Query("SELECT AVG(pm.sessionsAttended * 100.0 / pm.totalSessions) FROM ParticipationMetrics pm " +
           "WHERE pm.group = :group AND pm.periodYear = :year AND pm.periodMonth = :month AND pm.totalSessions > 0")
    Double calculateAverageAttendancePercentage(
            @Param("group") Group group, @Param("year") Integer year, @Param("month") Integer month);

    /**
     * Calculate average engagement score for a group in a specific period
     */
    @Query("SELECT AVG(pm.engagementScore) FROM ParticipationMetrics pm " +
           "WHERE pm.group = :group AND pm.periodYear = :year AND pm.periodMonth = :month")
    Double calculateAverageEngagementScore(
            @Param("group") Group group, @Param("year") Integer year, @Param("month") Integer month);

    /**
     * Find top participants by attendance percentage in a group for a specific period
     */
    @Query("SELECT pm FROM ParticipationMetrics pm " +
           "WHERE pm.group = :group AND pm.periodYear = :year AND pm.periodMonth = :month AND pm.totalSessions > 0 " +
           "ORDER BY (pm.sessionsAttended * 100.0 / pm.totalSessions) DESC")
    List<ParticipationMetrics> findTopParticipantsByAttendance(
            @Param("group") Group group, @Param("year") Integer year, @Param("month") Integer month);

    /**
     * Find top participants by engagement score in a group for a specific period
     */
    @Query("SELECT pm FROM ParticipationMetrics pm " +
           "WHERE pm.group = :group AND pm.periodYear = :year AND pm.periodMonth = :month " +
           "ORDER BY pm.engagementScore DESC")
    List<ParticipationMetrics> findTopParticipantsByEngagement(
            @Param("group") Group group, @Param("year") Integer year, @Param("month") Integer month);

    /**
     * Calculate total prayer requests submitted in a group for a specific period
     */
    @Query("SELECT SUM(pm.prayerRequestsSubmitted) FROM ParticipationMetrics pm " +
           "WHERE pm.group = :group AND pm.periodYear = :year AND pm.periodMonth = :month")
    Integer calculateTotalPrayerRequestsSubmitted(
            @Param("group") Group group, @Param("year") Integer year, @Param("month") Integer month);

    /**
     * Calculate total prayer requests answered in a group for a specific period
     */
    @Query("SELECT SUM(pm.prayerRequestsAnswered) FROM ParticipationMetrics pm " +
           "WHERE pm.group = :group AND pm.periodYear = :year AND pm.periodMonth = :month")
    Integer calculateTotalPrayerRequestsAnswered(
            @Param("group") Group group, @Param("year") Integer year, @Param("month") Integer month);
}