package com.brandyodhiambo.bibleApi.feature.analyticsmgt.models;

import com.brandyodhiambo.bibleApi.feature.studymgt.models.ReadingPlan;
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

/**
 * Entity to track user progress through reading plans
 */
@Entity
@Table(name = "reading_progress")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadingProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reading_plan_id", nullable = false)
    private ReadingPlan readingPlan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    /**
     * Percentage of completion (0-100)
     */
    @Column(name = "completion_percentage", nullable = false)
    private Integer completionPercentage;

    /**
     * Last completed reference in the reading plan
     */
    @Column(name = "last_completed_reference")
    private String lastCompletedReference;

    /**
     * Date when the user last made progress
     */
    @Column(name = "last_activity_date")
    private LocalDate lastActivityDate;

    /**
     * Whether the reading plan is completed by this user
     */
    @Column(name = "completed", nullable = false)
    private boolean completed;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}