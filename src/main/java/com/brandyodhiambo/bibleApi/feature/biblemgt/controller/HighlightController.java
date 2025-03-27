package com.brandyodhiambo.bibleApi.feature.biblemgt.controller;

import com.brandyodhiambo.bibleApi.feature.biblemgt.models.dto.HighlightResponse;
import com.brandyodhiambo.bibleApi.feature.biblemgt.service.HighlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/highlights")
@RequiredArgsConstructor
public class HighlightController {

    private final HighlightService highlightService;

    @PostMapping("/verses/{verseId}")
    public ResponseEntity<HighlightResponse> createHighlight(
            @PathVariable Long verseId,
            @RequestBody Map<String, String> payload) {
        String color = payload.get("color");
        return ResponseEntity.status(HttpStatus.CREATED).body(highlightService.createHighlight(verseId, color));
    }

    @GetMapping
    public ResponseEntity<List<HighlightResponse>> getHighlightsByUser() {
        return ResponseEntity.ok(highlightService.getHighlightsByUser());
    }

    @GetMapping("/all")
    public ResponseEntity<List<HighlightResponse>> getAllHighlights() {
        return ResponseEntity.ok(highlightService.getAllHighlights());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HighlightResponse> getHighlightById(@PathVariable Long id) {
        return ResponseEntity.ok(highlightService.getHighlightById(id));
    }

    @GetMapping("/verses/{verseId}")
    public ResponseEntity<HighlightResponse> getHighlightByVerseId(@PathVariable Long verseId) {
        return ResponseEntity.ok(highlightService.getHighlightByVerseId(verseId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HighlightResponse> updateHighlightColor(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {
        String color = payload.get("color");
        return ResponseEntity.ok(highlightService.updateHighlightColor(id, color));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHighlight(@PathVariable Long id) {
        highlightService.deleteHighlight(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/verses/{verseId}/check")
    public ResponseEntity<Map<String, Boolean>> isVerseHighlighted(@PathVariable Long verseId) {
        boolean isHighlighted = highlightService.isVerseHighlighted(verseId);
        return ResponseEntity.ok(Map.of("highlighted", isHighlighted));
    }
}