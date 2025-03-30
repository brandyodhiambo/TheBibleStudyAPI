package com.brandyodhiambo.bibleApi.feature.biblemgt.service;

import com.brandyodhiambo.bibleApi.feature.biblemgt.models.Highlight;
import com.brandyodhiambo.bibleApi.feature.biblemgt.models.Chapter;
import com.brandyodhiambo.bibleApi.feature.biblemgt.models.Book;
import com.brandyodhiambo.bibleApi.feature.biblemgt.models.Verse;
import com.brandyodhiambo.bibleApi.feature.biblemgt.models.dto.HighlightResponse;
import com.brandyodhiambo.bibleApi.feature.biblemgt.repository.HighlightRepository;
import com.brandyodhiambo.bibleApi.feature.biblemgt.repository.VerseRepository;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.Users;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HighlightServiceImplTest {

    @Mock
    private HighlightRepository highlightRepository;

    @Mock
    private VerseRepository verseRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private HighlightServiceImpl highlightService;

    private Users testUser;
    private Book testBook;
    private Chapter testChapter;
    private Verse testVerse;
    private Highlight testHighlight;

    @BeforeEach
    void setUp() {
        // Set up test User
        testUser = mock(Users.class);
        when(testUser.getId()).thenReturn(1L);
        when(testUser.getUsername()).thenReturn("testuser");
        when(testUser.getEmail()).thenReturn("test@example.com");

        // Set up test Book
        testBook = Book.builder()
                .id(1L)
                .name("Genesis")
                .abbreviation("Gen")
                .position(1)
                .build();

        // Set up test Chapter
        testChapter = Chapter.builder()
                .id(1L)
                .number(1)
                .book(testBook)
                .build();

        // Set up test Verse
        testVerse = Verse.builder()
                .id(1L)
                .number(1)
                .text("In the beginning God created the heaven and the earth.")
                .chapter(testChapter)
                .build();

        // Set up test Highlight
        testHighlight = Highlight.builder()
                .id(1L)
                .verse(testVerse)
                .user(testUser)
                .color("yellow")
                .createdAt(LocalDateTime.now())
                .build();

        // Mock SecurityContextHolder
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(testUser);
    }

    @Test
    void createHighlight_WhenVerseExistsAndNotHighlighted_ShouldCreateHighlight() {
        // Arrange
        Long verseId = 1L;
        String color = "yellow";
        when(verseRepository.findById(verseId)).thenReturn(Optional.of(testVerse));
        when(highlightRepository.existsByUserAndVerse(testUser, testVerse)).thenReturn(false);
        when(highlightRepository.save(any(Highlight.class))).thenReturn(testHighlight);

        // Act
        HighlightResponse result = highlightService.createHighlight(verseId, color);

        // Assert
        assertNotNull(result);
        assertEquals(testHighlight.getColor(), result.getColor());
        assertEquals(testVerse.getText(), result.getVerseText());
        verify(verseRepository).findById(verseId);
        verify(highlightRepository).existsByUserAndVerse(testUser, testVerse);
        verify(highlightRepository).save(any(Highlight.class));
    }

    @Test
    void createHighlight_WhenVerseExistsAndAlreadyHighlighted_ShouldUpdateColor() {
        // Arrange
        Long verseId = 1L;
        String newColor = "green";
        when(verseRepository.findById(verseId)).thenReturn(Optional.of(testVerse));
        when(highlightRepository.existsByUserAndVerse(testUser, testVerse)).thenReturn(true);
        when(highlightRepository.findByUserAndVerse(testUser, testVerse)).thenReturn(Optional.of(testHighlight));
        when(highlightRepository.save(testHighlight)).thenReturn(testHighlight);

        // Act
        HighlightResponse result = highlightService.createHighlight(verseId, newColor);

        // Assert
        assertNotNull(result);
        assertEquals(newColor, testHighlight.getColor());
        verify(verseRepository).findById(verseId);
        verify(highlightRepository).existsByUserAndVerse(testUser, testVerse);
        verify(highlightRepository).findByUserAndVerse(testUser, testVerse);
        verify(highlightRepository).save(testHighlight);
    }

    @Test
    void createHighlight_WhenVerseDoesNotExist_ShouldThrowException() {
        // Arrange
        Long verseId = 1L;
        String color = "yellow";
        when(verseRepository.findById(verseId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            highlightService.createHighlight(verseId, color);
        });
        verify(verseRepository).findById(verseId);
        verifyNoInteractions(highlightRepository);
    }

    @Test
    void getHighlightsByUser_ShouldReturnUserHighlights() {
        // Arrange
        List<Highlight> highlights = Arrays.asList(testHighlight);
        when(highlightRepository.findByUser(testUser)).thenReturn(highlights);

        // Act
        List<HighlightResponse> result = highlightService.getHighlightsByUser();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testHighlight.getColor(), result.get(0).getColor());
        verify(highlightRepository).findByUser(testUser);
    }

    @Test
    void getHighlightById_WhenHighlightExistsAndBelongsToUser_ShouldReturnHighlight() {
        // Arrange
        Long highlightId = 1L;
        when(highlightRepository.findById(highlightId)).thenReturn(Optional.of(testHighlight));

        // Act
        HighlightResponse result = highlightService.getHighlightById(highlightId);

        // Assert
        assertNotNull(result);
        assertEquals(testHighlight.getColor(), result.getColor());
        verify(highlightRepository).findById(highlightId);
    }

    @Test
    void getHighlightById_WhenHighlightDoesNotExist_ShouldThrowException() {
        // Arrange
        Long highlightId = 1L;
        when(highlightRepository.findById(highlightId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            highlightService.getHighlightById(highlightId);
        });
        verify(highlightRepository).findById(highlightId);
    }

    @Test
    void getHighlightById_WhenHighlightDoesNotBelongToUser_ShouldThrowException() {
        // Arrange
        Long highlightId = 1L;
        Users otherUser = mock(Users.class);
        when(otherUser.getId()).thenReturn(2L);
        Highlight otherHighlight = Highlight.builder()
                .id(highlightId)
                .verse(testVerse)
                .user(otherUser)
                .color("blue")
                .build();
        when(highlightRepository.findById(highlightId)).thenReturn(Optional.of(otherHighlight));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            highlightService.getHighlightById(highlightId);
        });
        verify(highlightRepository).findById(highlightId);
    }

    @Test
    void updateHighlightColor_WhenHighlightExistsAndBelongsToUser_ShouldUpdateColor() {
        // Arrange
        Long highlightId = 1L;
        String newColor = "green";
        when(highlightRepository.findById(highlightId)).thenReturn(Optional.of(testHighlight));
        when(highlightRepository.save(testHighlight)).thenReturn(testHighlight);

        // Act
        HighlightResponse result = highlightService.updateHighlightColor(highlightId, newColor);

        // Assert
        assertNotNull(result);
        assertEquals(newColor, testHighlight.getColor());
        verify(highlightRepository).findById(highlightId);
        verify(highlightRepository).save(testHighlight);
    }

    @Test
    void deleteHighlight_WhenHighlightExistsAndBelongsToUser_ShouldDeleteHighlight() {
        // Arrange
        Long highlightId = 1L;
        when(highlightRepository.findById(highlightId)).thenReturn(Optional.of(testHighlight));
        doNothing().when(highlightRepository).delete(testHighlight);

        // Act
        highlightService.deleteHighlight(highlightId);

        // Assert
        verify(highlightRepository).findById(highlightId);
        verify(highlightRepository).delete(testHighlight);
    }

    @Test
    void isVerseHighlighted_WhenVerseIsHighlighted_ShouldReturnTrue() {
        // Arrange
        Long verseId = 1L;
        when(verseRepository.findById(verseId)).thenReturn(Optional.of(testVerse));
        when(highlightRepository.existsByUserAndVerse(testUser, testVerse)).thenReturn(true);

        // Act
        boolean result = highlightService.isVerseHighlighted(verseId);

        // Assert
        assertTrue(result);
        verify(verseRepository).findById(verseId);
        verify(highlightRepository).existsByUserAndVerse(testUser, testVerse);
    }

    @Test
    void isVerseHighlighted_WhenVerseIsNotHighlighted_ShouldReturnFalse() {
        // Arrange
        Long verseId = 1L;
        when(verseRepository.findById(verseId)).thenReturn(Optional.of(testVerse));
        when(highlightRepository.existsByUserAndVerse(testUser, testVerse)).thenReturn(false);

        // Act
        boolean result = highlightService.isVerseHighlighted(verseId);

        // Assert
        assertFalse(result);
        verify(verseRepository).findById(verseId);
        verify(highlightRepository).existsByUserAndVerse(testUser, testVerse);
    }
}