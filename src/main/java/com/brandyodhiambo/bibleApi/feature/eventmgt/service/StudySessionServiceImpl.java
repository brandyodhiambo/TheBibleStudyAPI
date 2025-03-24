package com.brandyodhiambo.bibleApi.feature.eventmgt.service;

import com.brandyodhiambo.bibleApi.exception.ResourceNotFoundException;
import com.brandyodhiambo.bibleApi.feature.eventmgt.models.RSVPStatus;
import com.brandyodhiambo.bibleApi.feature.eventmgt.models.StudySession;
import com.brandyodhiambo.bibleApi.feature.eventmgt.models.dto.CreateSessionRequest;
import com.brandyodhiambo.bibleApi.feature.eventmgt.models.dto.SessionResponse;
import com.brandyodhiambo.bibleApi.feature.eventmgt.models.dto.UpdateSessionRequest;
import com.brandyodhiambo.bibleApi.feature.eventmgt.repository.SessionRSVPRepository;
import com.brandyodhiambo.bibleApi.feature.eventmgt.repository.StudySessionRepository;
import com.brandyodhiambo.bibleApi.feature.groupmgt.models.Group;
import com.brandyodhiambo.bibleApi.feature.groupmgt.repository.GroupRepository;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.RoleName;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.Users;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.dto.UserSummary;
import com.brandyodhiambo.bibleApi.feature.usermgt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudySessionServiceImpl implements StudySessionService {

    private final StudySessionRepository studySessionRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final SessionRSVPRepository sessionRSVPRepository;
    private final JavaMailSender mailSender;

    @Override
    @Transactional
    public SessionResponse createSession(CreateSessionRequest request, String username) {
        Users user = userRepository.getUserByName(username);
        Group group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", request.getGroupId().toString()));
        
        // Check if the user is the group leader or an admin
        boolean isAdmin = user.getRole().stream()
                .anyMatch(role -> role.getName() == RoleName.ROLE_ADMIN);
        
        if (!group.isLeader(user) && !isAdmin) {
            throw new AccessDeniedException("Only the group leader or an admin can create sessions");
        }
        
        StudySession session = new StudySession();
        session.setTitle(request.getTitle());
        session.setDescription(request.getDescription());
        session.setSessionDate(request.getSessionDate());
        session.setStartTime(request.getStartTime());
        session.setEndTime(request.getEndTime());
        session.setLocation(request.getLocation());
        session.setType(request.getType());
        session.setRecurrencePattern(request.getRecurrencePattern());
        session.setRecurrenceEndDate(request.getRecurrenceEndDate());
        session.setGroup(group);
        session.setCreatedBy(user);
        
        StudySession savedSession = studySessionRepository.save(session);
        
        return mapToSessionResponse(savedSession);
    }

    @Override
    @Transactional
    public SessionResponse updateSession(Long sessionId, UpdateSessionRequest request, String username) {
        StudySession session = studySessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Study Session", "id", sessionId.toString()));
        
        Users user = userRepository.getUserByName(username);
        
        // Check if the user is the session creator, group leader, or an admin
        boolean isAdmin = user.getRole().stream()
                .anyMatch(role -> role.getName() == RoleName.ROLE_ADMIN);
        
        if (!session.getCreatedBy().equals(user) && !session.getGroup().isLeader(user) && !isAdmin) {
            throw new AccessDeniedException("Only the session creator, group leader, or an admin can update sessions");
        }
        
        // Update the session fields if provided
        if (request.getTitle() != null) {
            session.setTitle(request.getTitle());
        }
        
        if (request.getDescription() != null) {
            session.setDescription(request.getDescription());
        }
        
        if (request.getSessionDate() != null) {
            session.setSessionDate(request.getSessionDate());
        }
        
        if (request.getStartTime() != null) {
            session.setStartTime(request.getStartTime());
        }
        
        if (request.getEndTime() != null) {
            session.setEndTime(request.getEndTime());
        }
        
        if (request.getLocation() != null) {
            session.setLocation(request.getLocation());
        }
        
        if (request.getType() != null) {
            session.setType(request.getType());
        }
        
        if (request.getRecurrencePattern() != null) {
            session.setRecurrencePattern(request.getRecurrencePattern());
        }
        
        if (request.getRecurrenceEndDate() != null) {
            session.setRecurrenceEndDate(request.getRecurrenceEndDate());
        }
        
        StudySession updatedSession = studySessionRepository.save(session);
        
        return mapToSessionResponse(updatedSession);
    }

    @Override
    @Transactional
    public void deleteSession(Long sessionId, String username) {
        StudySession session = studySessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Study Session", "id", sessionId.toString()));
        
        Users user = userRepository.getUserByName(username);
        
        // Check if the user is the session creator, group leader, or an admin
        boolean isAdmin = user.getRole().stream()
                .anyMatch(role -> role.getName() == RoleName.ROLE_ADMIN);
        
        if (!session.getCreatedBy().equals(user) && !session.getGroup().isLeader(user) && !isAdmin) {
            throw new AccessDeniedException("Only the session creator, group leader, or an admin can delete sessions");
        }
        
        studySessionRepository.delete(session);
    }

    @Override
    public SessionResponse getSession(Long sessionId) {
        StudySession session = studySessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Study Session", "id", sessionId.toString()));
        
        return mapToSessionResponse(session);
    }

    @Override
    public List<SessionResponse> getSessionsByGroup(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId.toString()));
        
        return studySessionRepository.findByGroup(group).stream()
                .map(this::mapToSessionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SessionResponse> getUpcomingSessionsByGroup(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId.toString()));
        
        return studySessionRepository.findUpcomingSessionsByGroup(group, LocalDate.now()).stream()
                .map(this::mapToSessionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SessionResponse> getUpcomingSessionsForUser(String username) {
        Users user = userRepository.getUserByName(username);
        
        return studySessionRepository.findUpcomingSessionsForUser(user, LocalDate.now()).stream()
                .map(this::mapToSessionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SessionResponse> getSessionsByDateRange(LocalDate startDate, LocalDate endDate) {
        return studySessionRepository.findSessionsByDateRange(startDate, endDate).stream()
                .map(this::mapToSessionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SessionResponse> getSessionsByDateRangeAndGroup(Long groupId, LocalDate startDate, LocalDate endDate) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId.toString()));
        
        return studySessionRepository.findSessionsByDateRangeAndGroup(group, startDate, endDate).stream()
                .map(this::mapToSessionResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Scheduled(cron = "0 0 8 * * ?") // Run at 8:00 AM every day
    public void sendSessionReminders() {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        
        // Get all sessions scheduled for tomorrow
        List<StudySession> tomorrowSessions = studySessionRepository.findSessionsByDateRange(tomorrow, tomorrow);
        
        for (StudySession session : tomorrowSessions) {
            // Send reminder to all members of the group
            for (Users member : session.getGroup().getMembers()) {
                sendReminderEmail(member, session);
            }
        }
    }
    
    // Helper method to send reminder email
    private void sendReminderEmail(Users user, StudySession session) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Reminder: " + session.getTitle());
        message.setFrom("System");
        message.setText(
                "Hello " + user.getFirstName() + ",\n\n" +
                "This is a reminder for the study session \"" + session.getTitle() + "\" tomorrow.\n\n" +
                "Date: " + session.getSessionDate() + "\n" +
                "Time: " + session.getStartTime() + " - " + (session.getEndTime() != null ? session.getEndTime() : "End time not specified") + "\n" +
                "Location: " + (session.getLocation() != null ? session.getLocation() : "Location not specified") + "\n\n" +
                "We look forward to seeing you!"
        );
        
        mailSender.send(message);
    }
    
    // Helper method to map StudySession entity to SessionResponse DTO
    private SessionResponse mapToSessionResponse(StudySession session) {
        UserSummary createdBySummary = mapToUserSummary(session.getCreatedBy());
        
        // Count RSVPs by status
        long attendingCount = sessionRSVPRepository.countBySessionAndStatus(session, RSVPStatus.ATTENDING);
        long notAttendingCount = sessionRSVPRepository.countBySessionAndStatus(session, RSVPStatus.NOT_ATTENDING);
        long maybeCount = sessionRSVPRepository.countBySessionAndStatus(session, RSVPStatus.MAYBE);
        
        return new SessionResponse(
                session.getId(),
                session.getTitle(),
                session.getDescription(),
                session.getSessionDate(),
                session.getStartTime(),
                session.getEndTime(),
                session.getLocation(),
                session.getType(),
                session.getRecurrencePattern(),
                session.getRecurrenceEndDate(),
                session.getGroup().getId(),
                session.getGroup().getName(),
                createdBySummary,
                (int) attendingCount,
                (int) notAttendingCount,
                (int) maybeCount,
                session.getCreatedAt(),
                session.getUpdatedAt()
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