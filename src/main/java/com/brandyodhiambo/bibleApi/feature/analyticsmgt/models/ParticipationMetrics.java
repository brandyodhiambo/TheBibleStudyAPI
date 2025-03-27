package com.brandyodhiambo.bibleApi.feature.analyticsmgt.models;

import com.brandyodhiambo.bibleApi.feature.groupmgt.models.Group;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;

/**
 * Entity to track user participation metrics within a group
 */
@Entity
@Table(name = "participation_metrics")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationMetrics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    /**
     * The year and month for which these metrics apply
     */
    @Column(name = "period_year", nullable = false)
    private Integer periodYear;

    @Column(name = "period_month", nullable = false)
    private Integer periodMonth;

    /**
     * Number of sessions attended in the period
     */
    @Column(name = "sessions_attended", nullable = false)
    private Integer sessionsAttended;

    /**
     * Total number of sessions in the period
     */
    @Column(name = "total_sessions", nullable = false)
    private Integer totalSessions;

    /**
     * Number of chat messages sent in the period
     */
    @Column(name = "chat_messages_sent", nullable = false)
    private Integer chatMessagesSent;

    /**
     * Number of prayer requests submitted in the period
     */
    @Column(name = "prayer_requests_submitted", nullable = false)
    private Integer prayerRequestsSubmitted;

    /**
     * Number of prayer requests marked as answered in the period
     */
    @Column(name = "prayer_requests_answered", nullable = false)
    private Integer prayerRequestsAnswered;

    /**
     * Number of comments made on study materials in the period
     */
    @Column(name = "study_comments_made", nullable = false)
    private Integer studyCommentsMade;

    /**
     * Calculated engagement score (0-100) based on various metrics
     */
    @Column(name = "engagement_score", nullable = false)
    private Integer engagementScore;

    /**
     * Date when these metrics were last updated
     */
    @Column(name = "last_updated_date", nullable = false)
    private LocalDate lastUpdatedDate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Calculate attendance percentage
     * @return Percentage of sessions attended (0-100)
     */
    @Transient
    public Integer getAttendancePercentage() {
        if (totalSessions == 0) {
            return 0;
        }
        return (int) Math.round((double) sessionsAttended / totalSessions * 100);
    }

    /**
     * Get the YearMonth representation of the period
     * @return YearMonth object representing the period
     */
    @Transient
    public YearMonth getPeriod() {
        return YearMonth.of(periodYear, periodMonth);
    }
}