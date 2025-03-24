package com.brandyodhiambo.bibleApi.feature.eventmgt.controller;

import com.brandyodhiambo.bibleApi.feature.eventmgt.models.RSVPStatus;
import com.brandyodhiambo.bibleApi.feature.eventmgt.models.dto.RSVPRequest;
import com.brandyodhiambo.bibleApi.feature.eventmgt.models.dto.RSVPResponse;
import com.brandyodhiambo.bibleApi.feature.eventmgt.service.SessionRSVPService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rsvps")
@RequiredArgsConstructor
public class SessionRSVPController {

    private final SessionRSVPService sessionRSVPService;

    @PostMapping("/sessions/{sessionId}")
    public ResponseEntity<RSVPResponse> submitRSVP(
            @PathVariable Long sessionId,
            @Valid @RequestBody RSVPRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        RSVPResponse response = sessionRSVPService.submitRSVP(sessionId, request, userDetails.getUsername());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{rsvpId}")
    public ResponseEntity<RSVPResponse> updateRSVP(
            @PathVariable Long rsvpId,
            @Valid @RequestBody RSVPRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        RSVPResponse response = sessionRSVPService.updateRSVP(rsvpId, request, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{rsvpId}")
    public ResponseEntity<Void> deleteRSVP(
            @PathVariable Long rsvpId,
            @AuthenticationPrincipal UserDetails userDetails) {
        sessionRSVPService.deleteRSVP(rsvpId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{rsvpId}")
    public ResponseEntity<RSVPResponse> getRSVP(@PathVariable Long rsvpId) {
        RSVPResponse response = sessionRSVPService.getRSVP(rsvpId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sessions/{sessionId}")
    public ResponseEntity<List<RSVPResponse>> getRSVPsBySession(@PathVariable Long sessionId) {
        List<RSVPResponse> rsvps = sessionRSVPService.getRSVPsBySession(sessionId);
        return ResponseEntity.ok(rsvps);
    }

    @GetMapping("/sessions/{sessionId}/status/{status}")
    public ResponseEntity<List<RSVPResponse>> getRSVPsBySessionAndStatus(
            @PathVariable Long sessionId,
            @PathVariable RSVPStatus status) {
        List<RSVPResponse> rsvps = sessionRSVPService.getRSVPsBySessionAndStatus(sessionId, status);
        return ResponseEntity.ok(rsvps);
    }

    @GetMapping("/user")
    public ResponseEntity<List<RSVPResponse>> getRSVPsByUser(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<RSVPResponse> rsvps = sessionRSVPService.getRSVPsByUser(userDetails.getUsername());
        return ResponseEntity.ok(rsvps);
    }

    @GetMapping("/sessions/{sessionId}/user")
    public ResponseEntity<RSVPResponse> getUserRSVPForSession(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal UserDetails userDetails) {
        RSVPResponse response = sessionRSVPService.getUserRSVPForSession(sessionId, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sessions/{sessionId}/count/{status}")
    public ResponseEntity<Long> countRSVPsBySessionAndStatus(
            @PathVariable Long sessionId,
            @PathVariable RSVPStatus status) {
        long count = sessionRSVPService.countRSVPsBySessionAndStatus(sessionId, status);
        return ResponseEntity.ok(count);
    }
}