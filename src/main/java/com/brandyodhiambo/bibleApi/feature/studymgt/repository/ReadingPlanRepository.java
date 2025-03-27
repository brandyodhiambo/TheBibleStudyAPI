package com.brandyodhiambo.bibleApi.feature.studymgt.repository;

import com.brandyodhiambo.bibleApi.feature.groupmgt.models.Group;
import com.brandyodhiambo.bibleApi.feature.studymgt.models.ReadingPlan;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReadingPlanRepository extends JpaRepository<ReadingPlan, Long> {
    
    List<ReadingPlan> findByGroup(Group group);
    
    List<ReadingPlan> findByCreatedBy(Users user);
    
    @Query("SELECT rp FROM ReadingPlan rp WHERE rp.group = :group AND rp.createdBy = :user")
    List<ReadingPlan> findByGroupAndCreatedBy(@Param("group") Group group, @Param("user") Users user);
    
    @Query("SELECT rp FROM ReadingPlan rp WHERE " +
           "LOWER(rp.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(rp.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(rp.bibleReferences) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(rp.topics) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<ReadingPlan> searchByTerm(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT rp FROM ReadingPlan rp WHERE rp.group = :group AND (" +
           "LOWER(rp.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(rp.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(rp.bibleReferences) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(rp.topics) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<ReadingPlan> searchByTermInGroup(@Param("searchTerm") String searchTerm, @Param("group") Group group);
    
    List<ReadingPlan> findByStartDateGreaterThanEqualAndEndDateLessThanEqual(LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT rp FROM ReadingPlan rp WHERE rp.group = :group AND " +
           "rp.startDate >= :startDate AND rp.endDate <= :endDate")
    List<ReadingPlan> findByGroupAndDateRange(@Param("group") Group group, 
                                             @Param("startDate") LocalDate startDate, 
                                             @Param("endDate") LocalDate endDate);
}