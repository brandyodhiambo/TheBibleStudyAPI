package com.brandyodhiambo.bibleApi.feature.eventmgt.service;

import com.brandyodhiambo.bibleApi.feature.eventmgt.models.StudySession;
import com.brandyodhiambo.bibleApi.feature.eventmgt.models.dto.CreateSessionRequest;
import com.brandyodhiambo.bibleApi.feature.eventmgt.models.dto.SessionResponse;
import com.brandyodhiambo.bibleApi.feature.eventmgt.models.dto.UpdateSessionRequest;

import java.time.LocalDate;
import java.util.List;

public interface StudySessionService {

    // Create a new study session
    SessionResponse createSession(CreateSessionRequest request, String username);

    // Update an existing study session
    SessionResponse updateSession(Long sessionId, UpdateSessionRequest request, String username);

    // Delete a study session
    void deleteSession(Long sessionId, String username);

    // Get a study session by ID
    SessionResponse getSession(Long sessionId);

    // Get all study sessions for a group
    List<SessionResponse> getSessionsByGroup(Long groupId);

    // Get upcoming study sessions for a group
    List<SessionResponse> getUpcomingSessionsByGroup(Long groupId);

    // Get upcoming study sessions for the current user
    List<SessionResponse> getUpcomingSessionsForUser(String username);

    // Get study sessions by date range
    List<SessionResponse> getSessionsByDateRange(LocalDate startDate, LocalDate endDate);

    // Get study sessions by date range for a specific group
    List<SessionResponse> getSessionsByDateRangeAndGroup(Long groupId, LocalDate startDate, LocalDate endDate);

    // Send notifications for upcoming sessions
    void sendSessionReminders();
}
