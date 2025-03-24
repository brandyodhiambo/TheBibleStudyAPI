package com.brandyodhiambo.bibleApi.feature.eventmgt.repository;

import com.brandyodhiambo.bibleApi.feature.eventmgt.models.RSVPStatus;
import com.brandyodhiambo.bibleApi.feature.eventmgt.models.SessionRSVP;
import com.brandyodhiambo.bibleApi.feature.eventmgt.models.StudySession;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRSVPRepository extends JpaRepository<SessionRSVP, Long> {
    
    // Find RSVP by session and user
    Optional<SessionRSVP> findBySessionAndUser(StudySession session, Users user);
    
    // Find all RSVPs for a session
    List<SessionRSVP> findBySession(StudySession session);
    
    // Find all RSVPs by a user
    List<SessionRSVP> findByUser(Users user);
    
    // Find all RSVPs for a session with a specific status
    List<SessionRSVP> findBySessionAndStatus(StudySession session, RSVPStatus status);
    
    // Count RSVPs for a session by status
    @Query("SELECT COUNT(r) FROM SessionRSVP r WHERE r.session = :session AND r.status = :status")
    long countBySessionAndStatus(@Param("session") StudySession session, @Param("status") RSVPStatus status);
    
    // Check if a user has RSVP'd to a session
    boolean existsBySessionAndUser(StudySession session, Users user);
    
    // Delete RSVP by session and user
    void deleteBySessionAndUser(StudySession session, Users user);
}