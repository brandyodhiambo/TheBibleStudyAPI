package com.brandyodhiambo.bibleApi.feature.studymgt.controller;

import com.brandyodhiambo.bibleApi.feature.studymgt.models.dto.ReadingPlanResponse;
import com.brandyodhiambo.bibleApi.feature.studymgt.service.ReadingPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reading-plans")
@RequiredArgsConstructor
public class ReadingPlanController {

    private final ReadingPlanService readingPlanService;

    @PostMapping
    @PreAuthorize("hasRole('LEADER') or hasRole('ADMIN')")
    public ResponseEntity<ReadingPlanResponse> createReadingPlan(
            @RequestParam("groupId") Long groupId,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("bibleReferences") String bibleReferences,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam("topics") String topics,
            Authentication authentication) {
        
        String username = authentication.getName();
        ReadingPlanResponse response = readingPlanService.createReadingPlan(
                groupId, title, description, bibleReferences, startDate, endDate, topics, username);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{planId}")
    public ResponseEntity<ReadingPlanResponse> getReadingPlan(@PathVariable Long planId) {
        ReadingPlanResponse response = readingPlanService.getReadingPlan(planId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<ReadingPlanResponse>> getReadingPlansByGroup(@PathVariable Long groupId) {
        List<ReadingPlanResponse> response = readingPlanService.getReadingPlansByGroup(groupId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user")
    public ResponseEntity<List<ReadingPlanResponse>> getReadingPlansByUser(Authentication authentication) {
        String username = authentication.getName();
        List<ReadingPlanResponse> response = readingPlanService.getReadingPlansByUser(username);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ReadingPlanResponse>> searchReadingPlans(@RequestParam("term") String searchTerm) {
        List<ReadingPlanResponse> response = readingPlanService.searchReadingPlans(searchTerm);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search/group/{groupId}")
    public ResponseEntity<List<ReadingPlanResponse>> searchReadingPlansInGroup(
            @RequestParam("term") String searchTerm,
            @PathVariable Long groupId) {
        List<ReadingPlanResponse> response = readingPlanService.searchReadingPlansInGroup(searchTerm, groupId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<ReadingPlanResponse>> getReadingPlansByDateRange(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<ReadingPlanResponse> response = readingPlanService.getReadingPlansByDateRange(startDate, endDate);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/group/{groupId}/date-range")
    public ResponseEntity<List<ReadingPlanResponse>> getReadingPlansByGroupAndDateRange(
            @PathVariable Long groupId,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<ReadingPlanResponse> response = readingPlanService.getReadingPlansByGroupAndDateRange(groupId, startDate, endDate);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{planId}")
    public ResponseEntity<ReadingPlanResponse> updateReadingPlan(
            @PathVariable Long planId,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "bibleReferences", required = false) String bibleReferences,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "topics", required = false) String topics,
            Authentication authentication) {
        
        String username = authentication.getName();
        ReadingPlanResponse response = readingPlanService.updateReadingPlan(
                planId, title, description, bibleReferences, startDate, endDate, topics, username);
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{planId}")
    public ResponseEntity<Void> deleteReadingPlan(
            @PathVariable Long planId,
            Authentication authentication) {
        
        String username = authentication.getName();
        readingPlanService.deleteReadingPlan(planId, username);
        
        return ResponseEntity.noContent().build();
    }
}