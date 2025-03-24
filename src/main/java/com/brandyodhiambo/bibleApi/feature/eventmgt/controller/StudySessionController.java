package com.brandyodhiambo.bibleApi.feature.eventmgt.controller;

import com.brandyodhiambo.bibleApi.feature.eventmgt.models.dto.CreateSessionRequest;
import com.brandyodhiambo.bibleApi.feature.eventmgt.models.dto.SessionResponse;
import com.brandyodhiambo.bibleApi.feature.eventmgt.models.dto.UpdateSessionRequest;
import com.brandyodhiambo.bibleApi.feature.eventmgt.service.StudySessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
public class StudySessionController {

    private final StudySessionService studySessionService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_LEADER', 'ROLE_ADMIN')")
    public ResponseEntity<SessionResponse> createSession(
            @Valid @RequestBody CreateSessionRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        SessionResponse response = studySessionService.createSession(request, userDetails.getUsername());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{sessionId}")
    @PreAuthorize("hasAnyRole('ROLE_LEADER', 'ROLE_ADMIN')")
    public ResponseEntity<SessionResponse> updateSession(
            @PathVariable Long sessionId,
            @Valid @RequestBody UpdateSessionRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        SessionResponse response = studySessionService.updateSession(sessionId, request, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{sessionId}")
    @PreAuthorize("hasAnyRole('ROLE_LEADER', 'ROLE_ADMIN')")
    public ResponseEntity<Void> deleteSession(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal UserDetails userDetails) {
        studySessionService.deleteSession(sessionId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<SessionResponse> getSession(@PathVariable Long sessionId) {
        SessionResponse response = studySessionService.getSession(sessionId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<SessionResponse>> getSessionsByGroup(@PathVariable Long groupId) {
        List<SessionResponse> sessions = studySessionService.getSessionsByGroup(groupId);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/group/{groupId}/upcoming")
    public ResponseEntity<List<SessionResponse>> getUpcomingSessionsByGroup(@PathVariable Long groupId) {
        List<SessionResponse> sessions = studySessionService.getUpcomingSessionsByGroup(groupId);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/user/upcoming")
    public ResponseEntity<List<SessionResponse>> getUpcomingSessionsForUser(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<SessionResponse> sessions = studySessionService.getUpcomingSessionsForUser(userDetails.getUsername());
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<SessionResponse>> getSessionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<SessionResponse> sessions = studySessionService.getSessionsByDateRange(startDate, endDate);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/group/{groupId}/date-range")
    public ResponseEntity<List<SessionResponse>> getSessionsByDateRangeAndGroup(
            @PathVariable Long groupId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<SessionResponse> sessions = studySessionService.getSessionsByDateRangeAndGroup(groupId, startDate, endDate);
        return ResponseEntity.ok(sessions);
    }

    @PostMapping("/send-reminders")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> sendSessionReminders() {
        studySessionService.sendSessionReminders();
        return ResponseEntity.ok().build();
    }
}