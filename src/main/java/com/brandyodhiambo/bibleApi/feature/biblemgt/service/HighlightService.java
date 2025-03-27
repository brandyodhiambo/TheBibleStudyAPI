package com.brandyodhiambo.bibleApi.feature.biblemgt.service;

import com.brandyodhiambo.bibleApi.feature.biblemgt.models.dto.HighlightResponse;

import java.util.List;

public interface HighlightService {
    // Create a highlight
    HighlightResponse createHighlight(Long verseId, String color);
    
    // Get highlights
    List<HighlightResponse> getAllHighlights();
    List<HighlightResponse> getHighlightsByUser();
    HighlightResponse getHighlightById(Long id);
    HighlightResponse getHighlightByVerseId(Long verseId);
    
    // Update a highlight
    HighlightResponse updateHighlightColor(Long id, String color);
    
    // Delete a highlight
    void deleteHighlight(Long id);
    
    // Check if a verse is highlighted by the current user
    boolean isVerseHighlighted(Long verseId);
}