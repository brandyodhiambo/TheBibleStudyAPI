package com.brandyodhiambo.bibleApi.feature.prayermgt.controller;

import com.brandyodhiambo.bibleApi.feature.prayermgt.models.dto.PrayerRequestResponse;
import com.brandyodhiambo.bibleApi.feature.prayermgt.service.PrayerRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/prayer-requests")
@RequiredArgsConstructor
public class PrayerRequestController {

    private final PrayerRequestService prayerRequestService;

    @PostMapping("/groups/{groupId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PrayerRequestResponse> createPrayerRequest(
            @PathVariable Long groupId,
            @RequestBody Map<String, String> payload) {
        String title = payload.get("title");
        String description = payload.get("description");
        
        if (title == null || title.trim().isEmpty() || description == null || description.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        PrayerRequestResponse prayerRequest = prayerRequestService.createPrayerRequest(groupId, title, description);
        return ResponseEntity.status(HttpStatus.CREATED).body(prayerRequest);
    }

    @GetMapping("/groups/{groupId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PrayerRequestResponse>> getGroupPrayerRequests(@PathVariable Long groupId) {
        List<PrayerRequestResponse> prayerRequests = prayerRequestService.getGroupPrayerRequests(groupId);
        return ResponseEntity.ok(prayerRequests);
    }

    @GetMapping("/groups/{groupId}/answered/{answered}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PrayerRequestResponse>> getGroupPrayerRequestsByAnswered(
            @PathVariable Long groupId,
            @PathVariable boolean answered) {
        List<PrayerRequestResponse> prayerRequests = prayerRequestService.getGroupPrayerRequestsByAnswered(groupId, answered);
        return ResponseEntity.ok(prayerRequests);
    }

    @GetMapping("/{prayerRequestId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PrayerRequestResponse> getPrayerRequest(@PathVariable Long prayerRequestId) {
        PrayerRequestResponse prayerRequest = prayerRequestService.getPrayerRequest(prayerRequestId);
        return ResponseEntity.ok(prayerRequest);
    }

    @PutMapping("/{prayerRequestId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PrayerRequestResponse> updatePrayerRequest(
            @PathVariable Long prayerRequestId,
            @RequestBody Map<String, String> payload) {
        String title = payload.get("title");
        String description = payload.get("description");
        
        if (title == null || title.trim().isEmpty() || description == null || description.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        PrayerRequestResponse prayerRequest = prayerRequestService.updatePrayerRequest(prayerRequestId, title, description);
        return ResponseEntity.ok(prayerRequest);
    }

    @PutMapping("/{prayerRequestId}/mark-answered")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PrayerRequestResponse> markAsAnswered(
            @PathVariable Long prayerRequestId,
            @RequestBody Map<String, String> payload) {
        String testimony = payload.get("testimony");
        
        if (testimony == null) {
            testimony = ""; // Allow empty testimony
        }
        
        PrayerRequestResponse prayerRequest = prayerRequestService.markAsAnswered(prayerRequestId, testimony);
        return ResponseEntity.ok(prayerRequest);
    }

    @DeleteMapping("/{prayerRequestId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deletePrayerRequest(@PathVariable Long prayerRequestId) {
        prayerRequestService.deletePrayerRequest(prayerRequestId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PrayerRequestResponse>> getUserPrayerRequests() {
        List<PrayerRequestResponse> prayerRequests = prayerRequestService.getUserPrayerRequests();
        return ResponseEntity.ok(prayerRequests);
    }

    @GetMapping("/user/answered/{answered}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PrayerRequestResponse>> getUserPrayerRequestsByAnswered(@PathVariable boolean answered) {
        List<PrayerRequestResponse> prayerRequests = prayerRequestService.getUserPrayerRequestsByAnswered(answered);
        return ResponseEntity.ok(prayerRequests);
    }
}