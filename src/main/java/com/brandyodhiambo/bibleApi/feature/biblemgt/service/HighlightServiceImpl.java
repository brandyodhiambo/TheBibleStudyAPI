package com.brandyodhiambo.bibleApi.feature.biblemgt.service;

import com.brandyodhiambo.bibleApi.feature.biblemgt.models.Highlight;
import com.brandyodhiambo.bibleApi.feature.biblemgt.models.Verse;
import com.brandyodhiambo.bibleApi.feature.biblemgt.models.dto.HighlightResponse;
import com.brandyodhiambo.bibleApi.feature.biblemgt.repository.HighlightRepository;
import com.brandyodhiambo.bibleApi.feature.biblemgt.repository.VerseRepository;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.Users;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HighlightServiceImpl implements HighlightService {

    private final HighlightRepository highlightRepository;
    private final VerseRepository verseRepository;

    @Override
    public HighlightResponse createHighlight(Long verseId, String color) {
        Users currentUser = getCurrentUser();
        
        Verse verse = verseRepository.findById(verseId)
                .orElseThrow(() -> new EntityNotFoundException("Verse not found with id: " + verseId));
        
        // Check if highlight already exists
        if (highlightRepository.existsByUserAndVerse(currentUser, verse)) {
            // Update the color of the existing highlight
            Highlight existingHighlight = highlightRepository.findByUserAndVerse(currentUser, verse)
                    .orElseThrow(() -> new EntityNotFoundException("Highlight not found for verse id: " + verseId));
            existingHighlight.setColor(color);
            Highlight updatedHighlight = highlightRepository.save(existingHighlight);
            return mapToHighlightResponse(updatedHighlight);
        }
        
        Highlight highlight = Highlight.builder()
                .verse(verse)
                .user(currentUser)
                .color(color)
                .build();
        
        Highlight savedHighlight = highlightRepository.save(highlight);
        return mapToHighlightResponse(savedHighlight);
    }

    @Override
    public List<HighlightResponse> getAllHighlights() {
        return highlightRepository.findAll().stream()
                .map(this::mapToHighlightResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<HighlightResponse> getHighlightsByUser() {
        Users currentUser = getCurrentUser();
        return highlightRepository.findByUser(currentUser).stream()
                .map(this::mapToHighlightResponse)
                .collect(Collectors.toList());
    }

    @Override
    public HighlightResponse getHighlightById(Long id) {
        Users currentUser = getCurrentUser();
        
        Highlight highlight = highlightRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Highlight not found with id: " + id));
        
        // Check if the highlight belongs to the current user
        if (!highlight.getUser().equals(currentUser)) {
            throw new IllegalStateException("You can only access your own highlights");
        }
        
        return mapToHighlightResponse(highlight);
    }

    @Override
    public HighlightResponse getHighlightByVerseId(Long verseId) {
        Users currentUser = getCurrentUser();
        
        Verse verse = verseRepository.findById(verseId)
                .orElseThrow(() -> new EntityNotFoundException("Verse not found with id: " + verseId));
        
        Highlight highlight = highlightRepository.findByUserAndVerse(currentUser, verse)
                .orElseThrow(() -> new EntityNotFoundException("Highlight not found for verse id: " + verseId));
        
        return mapToHighlightResponse(highlight);
    }

    @Override
    public HighlightResponse updateHighlightColor(Long id, String color) {
        Users currentUser = getCurrentUser();
        
        Highlight highlight = highlightRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Highlight not found with id: " + id));
        
        // Check if the highlight belongs to the current user
        if (!highlight.getUser().equals(currentUser)) {
            throw new IllegalStateException("You can only update your own highlights");
        }
        
        highlight.setColor(color);
        Highlight updatedHighlight = highlightRepository.save(highlight);
        
        return mapToHighlightResponse(updatedHighlight);
    }

    @Override
    public void deleteHighlight(Long id) {
        Users currentUser = getCurrentUser();
        
        Highlight highlight = highlightRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Highlight not found with id: " + id));
        
        // Check if the highlight belongs to the current user
        if (!highlight.getUser().equals(currentUser)) {
            throw new IllegalStateException("You can only delete your own highlights");
        }
        
        highlightRepository.delete(highlight);
    }

    @Override
    public boolean isVerseHighlighted(Long verseId) {
        Users currentUser = getCurrentUser();
        
        Verse verse = verseRepository.findById(verseId)
                .orElseThrow(() -> new EntityNotFoundException("Verse not found with id: " + verseId));
        
        return highlightRepository.existsByUserAndVerse(currentUser, verse);
    }
    
    private Users getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (Users) authentication.getPrincipal();
    }
    
    private HighlightResponse mapToHighlightResponse(Highlight highlight) {
        Verse verse = highlight.getVerse();
        
        return HighlightResponse.builder()
                .id(highlight.getId())
                .verseId(verse.getId())
                .verseNumber(verse.getNumber())
                .verseText(verse.getText())
                .chapterId(verse.getChapter().getId())
                .chapterNumber(verse.getChapter().getNumber())
                .bookId(verse.getChapter().getBook().getId())
                .bookName(verse.getChapter().getBook().getName())
                .userId(highlight.getUser().getId())
                .username(highlight.getUser().getUsername())
                .color(highlight.getColor())
                .createdAt(highlight.getCreatedAt())
                .build();
    }
}