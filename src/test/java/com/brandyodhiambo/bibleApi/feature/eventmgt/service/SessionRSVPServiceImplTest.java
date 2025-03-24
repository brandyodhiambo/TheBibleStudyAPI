package com.brandyodhiambo.bibleApi.feature.eventmgt.service;

import com.brandyodhiambo.bibleApi.exception.ResourceNotFoundException;
import com.brandyodhiambo.bibleApi.feature.eventmgt.models.RSVPStatus;
import com.brandyodhiambo.bibleApi.feature.eventmgt.models.SessionRSVP;
import com.brandyodhiambo.bibleApi.feature.eventmgt.models.StudySession;
import com.brandyodhiambo.bibleApi.feature.eventmgt.models.dto.RSVPRequest;
import com.brandyodhiambo.bibleApi.feature.eventmgt.models.dto.RSVPResponse;
import com.brandyodhiambo.bibleApi.feature.eventmgt.repository.SessionRSVPRepository;
import com.brandyodhiambo.bibleApi.feature.eventmgt.repository.StudySessionRepository;
import com.brandyodhiambo.bibleApi.feature.groupmgt.models.Group;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.Users;
import com.brandyodhiambo.bibleApi.feature.usermgt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionRSVPServiceImplTest {

    @Mock
    private SessionRSVPRepository sessionRSVPRepository;

    @Mock
    private StudySessionRepository studySessionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SessionRSVPServiceImpl sessionRSVPService;

    private Users testUser;
    private Users otherUser;
    private Group testGroup;
    private StudySession testSession;
    private SessionRSVP testRSVP;
    private RSVPRequest rsvpRequest;

    @BeforeEach
    void setUp() {
        // Setup test user
        testUser = new Users();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");

        // Setup other user
        otherUser = new Users();
        otherUser.setUsername("otheruser");
        otherUser.setEmail("other@example.com");
        otherUser.setFirstName("Other");
        otherUser.setLastName("User");

        // Setup RSVP request
        rsvpRequest = new RSVPRequest(RSVPStatus.ATTENDING, "Looking forward to it!");
    }

    @Test
    void submitRSVP_WhenNewRSVP_CreatesRSVP() {
        // Arrange
        Group group = mock(Group.class);
        when(group.isMember(testUser)).thenReturn(true);

        StudySession session = mock(StudySession.class);
        when(session.getGroup()).thenReturn(group);
        when(session.getId()).thenReturn(1L);
        when(session.getTitle()).thenReturn("Test Session");

        SessionRSVP rsvp = new SessionRSVP(session, testUser, RSVPStatus.ATTENDING, "Looking forward to it!");

        when(studySessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.getUserByName("testuser")).thenReturn(testUser);
        when(sessionRSVPRepository.findBySessionAndUser(session, testUser)).thenReturn(Optional.empty());
        when(sessionRSVPRepository.save(any(SessionRSVP.class))).thenReturn(rsvp);

        // Act
        RSVPResponse response = sessionRSVPService.submitRSVP(1L, rsvpRequest, "testuser");

        // Assert
        assertNotNull(response);
        assertEquals(RSVPStatus.ATTENDING, response.getStatus());
        assertEquals("Looking forward to it!", response.getComment());
        verify(sessionRSVPRepository).save(any(SessionRSVP.class));
    }

    @Test
    void submitRSVP_WhenExistingRSVP_UpdatesRSVP() {
        // Arrange
        Group group = mock(Group.class);
        when(group.isMember(testUser)).thenReturn(true);

        StudySession session = mock(StudySession.class);
        when(session.getGroup()).thenReturn(group);
        when(session.getId()).thenReturn(1L);
        when(session.getTitle()).thenReturn("Test Session");

        SessionRSVP existingRsvp = mock(SessionRSVP.class);

        // Create a new RSVP with updated values
        SessionRSVP updatedRsvp = new SessionRSVP(session, testUser, RSVPStatus.MAYBE, "Not sure if I can make it");

        when(studySessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.getUserByName("testuser")).thenReturn(testUser);
        when(sessionRSVPRepository.findBySessionAndUser(session, testUser)).thenReturn(Optional.of(existingRsvp));
        when(sessionRSVPRepository.save(existingRsvp)).thenReturn(updatedRsvp);

        // Update request
        RSVPRequest updateRequest = new RSVPRequest(RSVPStatus.MAYBE, "Not sure if I can make it");

        // Act
        RSVPResponse response = sessionRSVPService.submitRSVP(1L, updateRequest, "testuser");

        // Assert
        assertNotNull(response);
        assertEquals(RSVPStatus.MAYBE, response.getStatus());
        assertEquals("Not sure if I can make it", response.getComment());
        verify(sessionRSVPRepository).save(existingRsvp);
    }

    @Test
    void submitRSVP_WhenUserNotInGroup_ThrowsAccessDeniedException() {
        // Arrange
        Group group = mock(Group.class);
        when(group.isMember(otherUser)).thenReturn(false);

        StudySession session = mock(StudySession.class);
        when(session.getGroup()).thenReturn(group);

        when(studySessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.getUserByName("otheruser")).thenReturn(otherUser);

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> {
            sessionRSVPService.submitRSVP(1L, rsvpRequest, "otheruser");
        });
        verify(sessionRSVPRepository, never()).save(any(SessionRSVP.class));
    }

    @Test
    void updateRSVP_WhenUserOwnsRSVP_UpdatesRSVP() {
        // Arrange
        StudySession session = mock(StudySession.class);
        when(session.getId()).thenReturn(1L);
        when(session.getTitle()).thenReturn("Test Session");

        SessionRSVP existingRsvp = mock(SessionRSVP.class);
        when(existingRsvp.getUser()).thenReturn(testUser);

        // Create a new RSVP with updated values
        SessionRSVP updatedRsvp = new SessionRSVP(session, testUser, RSVPStatus.NOT_ATTENDING, "Can't make it");

        when(sessionRSVPRepository.findById(1L)).thenReturn(Optional.of(existingRsvp));
        when(userRepository.getUserByName("testuser")).thenReturn(testUser);
        when(sessionRSVPRepository.save(existingRsvp)).thenReturn(updatedRsvp);

        // Update request
        RSVPRequest updateRequest = new RSVPRequest(RSVPStatus.NOT_ATTENDING, "Can't make it");

        // Act
        RSVPResponse response = sessionRSVPService.updateRSVP(1L, updateRequest, "testuser");

        // Assert
        assertNotNull(response);
        assertEquals(RSVPStatus.NOT_ATTENDING, response.getStatus());
        assertEquals("Can't make it", response.getComment());
        verify(sessionRSVPRepository).save(existingRsvp);
    }
}
