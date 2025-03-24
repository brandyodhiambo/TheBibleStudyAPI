package com.brandyodhiambo.bibleApi.feature.eventmgt.service;

import com.brandyodhiambo.bibleApi.feature.eventmgt.models.RSVPStatus;
import com.brandyodhiambo.bibleApi.feature.eventmgt.models.dto.RSVPRequest;
import com.brandyodhiambo.bibleApi.feature.eventmgt.models.dto.RSVPResponse;

import java.util.List;

public interface SessionRSVPService {
    
    // Submit an RSVP for a session
    RSVPResponse submitRSVP(Long sessionId, RSVPRequest request, String username);
    
    // Update an existing RSVP
    RSVPResponse updateRSVP(Long rsvpId, RSVPRequest request, String username);
    
    // Delete an RSVP
    void deleteRSVP(Long rsvpId, String username);
    
    // Get an RSVP by ID
    RSVPResponse getRSVP(Long rsvpId);
    
    // Get all RSVPs for a session
    List<RSVPResponse> getRSVPsBySession(Long sessionId);
    
    // Get all RSVPs for a session with a specific status
    List<RSVPResponse> getRSVPsBySessionAndStatus(Long sessionId, RSVPStatus status);
    
    // Get all RSVPs by a user
    List<RSVPResponse> getRSVPsByUser(String username);
    
    // Get a user's RSVP for a specific session
    RSVPResponse getUserRSVPForSession(Long sessionId, String username);
    
    // Count RSVPs for a session by status
    long countRSVPsBySessionAndStatus(Long sessionId, RSVPStatus status);
}