package com.brandyodhiambo.bibleApi.feature.analyticsmgt.repository;

import com.brandyodhiambo.bibleApi.feature.analyticsmgt.models.ReadingProgress;
import com.brandyodhiambo.bibleApi.feature.groupmgt.models.Group;
import com.brandyodhiambo.bibleApi.feature.studymgt.models.ReadingPlan;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReadingProgressRepository extends JpaRepository<ReadingProgress, Long> {

    /**
     * Find reading progress by reading plan and user
     */
    Optional<ReadingProgress> findByReadingPlanAndUser(ReadingPlan readingPlan, Users user);

    /**
     * Find all reading progress entries for a specific user
     */
    List<ReadingProgress> findByUser(Users user);

    /**
     * Find all reading progress entries for a specific reading plan
     */
    List<ReadingProgress> findByReadingPlan(ReadingPlan readingPlan);

    /**
     * Find all reading progress entries for a specific reading plan with completion status
     */
    List<ReadingProgress> findByReadingPlanAndCompleted(ReadingPlan readingPlan, boolean completed);

    /**
     * Find all reading progress entries for a specific user with completion status
     */
    List<ReadingProgress> findByUserAndCompleted(Users user, boolean completed);

    /**
     * Find all reading progress entries for users in a specific group
     */
    @Query("SELECT rp FROM ReadingProgress rp WHERE rp.readingPlan.group = :group")
    List<ReadingProgress> findByGroup(@Param("group") Group group);

    /**
     * Find all reading progress entries for users in a specific group with completion status
     */
    @Query("SELECT rp FROM ReadingProgress rp WHERE rp.readingPlan.group = :group AND rp.completed = :completed")
    List<ReadingProgress> findByGroupAndCompleted(@Param("group") Group group, @Param("completed") boolean completed);

    /**
     * Find all reading progress entries updated after a specific date
     */
    List<ReadingProgress> findByLastActivityDateAfter(LocalDate date);

    /**
     * Calculate average completion percentage for a reading plan
     */
    @Query("SELECT AVG(rp.completionPercentage) FROM ReadingProgress rp WHERE rp.readingPlan = :readingPlan")
    Double calculateAverageCompletionPercentage(@Param("readingPlan") ReadingPlan readingPlan);

    /**
     * Calculate average completion percentage for all reading plans in a group
     */
    @Query("SELECT AVG(rp.completionPercentage) FROM ReadingProgress rp WHERE rp.readingPlan.group = :group")
    Double calculateAverageCompletionPercentageForGroup(@Param("group") Group group);

    /**
     * Count number of completed reading plans for a user
     */
    long countByUserAndCompleted(Users user, boolean completed);

    /**
     * Count number of completed reading plans in a group
     */
    @Query("SELECT COUNT(rp) FROM ReadingProgress rp WHERE rp.readingPlan.group = :group AND rp.completed = :completed")
    long countByGroupAndCompleted(@Param("group") Group group, @Param("completed") boolean completed);
}