package com.brandyodhiambo.bibleApi.feature.eventmgt.repository;

import com.brandyodhiambo.bibleApi.feature.eventmgt.models.StudySession;
import com.brandyodhiambo.bibleApi.feature.groupmgt.models.Group;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StudySessionRepository extends JpaRepository<StudySession, Long> {
    
    // Find sessions by group
    List<StudySession> findByGroup(Group group);
    
    // Find sessions created by a specific user
    List<StudySession> findByCreatedBy(Users user);
    
    // Find upcoming sessions for a group
    @Query("SELECT s FROM StudySession s WHERE s.group = :group AND s.sessionDate >= :today ORDER BY s.sessionDate ASC, s.startTime ASC")
    List<StudySession> findUpcomingSessionsByGroup(@Param("group") Group group, @Param("today") LocalDate today);
    
    // Find upcoming sessions for a user (sessions from groups they are members of)
    @Query("SELECT s FROM StudySession s JOIN s.group g JOIN g.members m WHERE m = :user AND s.sessionDate >= :today ORDER BY s.sessionDate ASC, s.startTime ASC")
    List<StudySession> findUpcomingSessionsForUser(@Param("user") Users user, @Param("today") LocalDate today);
    
    // Find sessions by date range
    @Query("SELECT s FROM StudySession s WHERE s.sessionDate BETWEEN :startDate AND :endDate ORDER BY s.sessionDate ASC, s.startTime ASC")
    List<StudySession> findSessionsByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    // Find sessions by date range for a specific group
    @Query("SELECT s FROM StudySession s WHERE s.group = :group AND s.sessionDate BETWEEN :startDate AND :endDate ORDER BY s.sessionDate ASC, s.startTime ASC")
    List<StudySession> findSessionsByDateRangeAndGroup(@Param("group") Group group, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}