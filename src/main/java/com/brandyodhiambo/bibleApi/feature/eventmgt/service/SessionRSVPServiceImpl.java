package com.brandyodhiambo.bibleApi.feature.eventmgt.service;

import com.brandyodhiambo.bibleApi.exception.ResourceNotFoundException;
import com.brandyodhiambo.bibleApi.feature.eventmgt.models.RSVPStatus;
import com.brandyodhiambo.bibleApi.feature.eventmgt.models.SessionRSVP;
import com.brandyodhiambo.bibleApi.feature.eventmgt.models.StudySession;
import com.brandyodhiambo.bibleApi.feature.eventmgt.models.dto.RSVPRequest;
import com.brandyodhiambo.bibleApi.feature.eventmgt.models.dto.RSVPResponse;
import com.brandyodhiambo.bibleApi.feature.eventmgt.repository.SessionRSVPRepository;
import com.brandyodhiambo.bibleApi.feature.eventmgt.repository.StudySessionRepository;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.Users;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.dto.UserSummary;
import com.brandyodhiambo.bibleApi.feature.usermgt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SessionRSVPServiceImpl implements SessionRSVPService {

    private final SessionRSVPRepository sessionRSVPRepository;
    private final StudySessionRepository studySessionRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public RSVPResponse submitRSVP(Long sessionId, RSVPRequest request, String username) {
        StudySession session = studySessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Study Session", "id", sessionId.toString()));
        
        Users user = userRepository.getUserByName(username);
        
        // Check if the user is a member of the group
        if (!session.getGroup().isMember(user)) {
            throw new AccessDeniedException("Only group members can RSVP to sessions");
        }
        
        // Check if the user already has an RSVP for this session
        SessionRSVP existingRSVP = sessionRSVPRepository.findBySessionAndUser(session, user).orElse(null);
        
        if (existingRSVP != null) {
            // Update existing RSVP
            existingRSVP.setStatus(request.getStatus());
            existingRSVP.setComment(request.getComment());
            
            SessionRSVP updatedRSVP = sessionRSVPRepository.save(existingRSVP);
            return mapToRSVPResponse(updatedRSVP);
        } else {
            // Create new RSVP
            SessionRSVP rsvp = new SessionRSVP(session, user, request.getStatus(), request.getComment());
            
            SessionRSVP savedRSVP = sessionRSVPRepository.save(rsvp);
            return mapToRSVPResponse(savedRSVP);
        }
    }

    @Override
    @Transactional
    public RSVPResponse updateRSVP(Long rsvpId, RSVPRequest request, String username) {
        SessionRSVP rsvp = sessionRSVPRepository.findById(rsvpId)
                .orElseThrow(() -> new ResourceNotFoundException("RSVP", "id", rsvpId.toString()));
        
        Users user = userRepository.getUserByName(username);
        
        // Check if the RSVP belongs to the user
        if (!rsvp.getUser().equals(user)) {
            throw new AccessDeniedException("You can only update your own RSVPs");
        }
        
        rsvp.setStatus(request.getStatus());
        rsvp.setComment(request.getComment());
        
        SessionRSVP updatedRSVP = sessionRSVPRepository.save(rsvp);
        return mapToRSVPResponse(updatedRSVP);
    }

    @Override
    @Transactional
    public void deleteRSVP(Long rsvpId, String username) {
        SessionRSVP rsvp = sessionRSVPRepository.findById(rsvpId)
                .orElseThrow(() -> new ResourceNotFoundException("RSVP", "id", rsvpId.toString()));
        
        Users user = userRepository.getUserByName(username);
        
        // Check if the RSVP belongs to the user
        if (!rsvp.getUser().equals(user)) {
            throw new AccessDeniedException("You can only delete your own RSVPs");
        }
        
        sessionRSVPRepository.delete(rsvp);
    }

    @Override
    public RSVPResponse getRSVP(Long rsvpId) {
        SessionRSVP rsvp = sessionRSVPRepository.findById(rsvpId)
                .orElseThrow(() -> new ResourceNotFoundException("RSVP", "id", rsvpId.toString()));
        
        return mapToRSVPResponse(rsvp);
    }

    @Override
    public List<RSVPResponse> getRSVPsBySession(Long sessionId) {
        StudySession session = studySessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Study Session", "id", sessionId.toString()));
        
        return sessionRSVPRepository.findBySession(session).stream()
                .map(this::mapToRSVPResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RSVPResponse> getRSVPsBySessionAndStatus(Long sessionId, RSVPStatus status) {
        StudySession session = studySessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Study Session", "id", sessionId.toString()));
        
        return sessionRSVPRepository.findBySessionAndStatus(session, status).stream()
                .map(this::mapToRSVPResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RSVPResponse> getRSVPsByUser(String username) {
        Users user = userRepository.getUserByName(username);
        
        return sessionRSVPRepository.findByUser(user).stream()
                .map(this::mapToRSVPResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RSVPResponse getUserRSVPForSession(Long sessionId, String username) {
        StudySession session = studySessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Study Session", "id", sessionId.toString()));
        
        Users user = userRepository.getUserByName(username);
        
        SessionRSVP rsvp = sessionRSVPRepository.findBySessionAndUser(session, user)
                .orElseThrow(() -> new ResourceNotFoundException("RSVP", "session and user", sessionId + " and " + username));
        
        return mapToRSVPResponse(rsvp);
    }

    @Override
    public long countRSVPsBySessionAndStatus(Long sessionId, RSVPStatus status) {
        StudySession session = studySessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Study Session", "id", sessionId.toString()));
        
        return sessionRSVPRepository.countBySessionAndStatus(session, status);
    }
    
    // Helper method to map SessionRSVP entity to RSVPResponse DTO
    private RSVPResponse mapToRSVPResponse(SessionRSVP rsvp) {
        UserSummary userSummary = mapToUserSummary(rsvp.getUser());
        
        return new RSVPResponse(
                rsvp.getId(),
                rsvp.getSession().getId(),
                rsvp.getSession().getTitle(),
                userSummary,
                rsvp.getStatus(),
                rsvp.getComment(),
                rsvp.getCreatedAt(),
                rsvp.getUpdatedAt()
        );
    }
    
    // Helper method to map Users entity to UserSummary DTO
    private UserSummary mapToUserSummary(Users user) {
        return new UserSummary(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail()
        );
    }
}