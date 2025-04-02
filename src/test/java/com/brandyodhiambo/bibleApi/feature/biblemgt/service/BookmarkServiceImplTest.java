package com.brandyodhiambo.bibleApi.feature.biblemgt.service;

import com.brandyodhiambo.bibleApi.feature.biblemgt.models.Bookmark;
import com.brandyodhiambo.bibleApi.feature.biblemgt.models.Chapter;
import com.brandyodhiambo.bibleApi.feature.biblemgt.models.Book;
import com.brandyodhiambo.bibleApi.feature.biblemgt.models.Verse;
import com.brandyodhiambo.bibleApi.feature.biblemgt.models.dto.BookmarkResponse;
import com.brandyodhiambo.bibleApi.feature.biblemgt.repository.BookmarkRepository;
import com.brandyodhiambo.bibleApi.feature.biblemgt.repository.VerseRepository;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.Users;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
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
@MockitoSettings(strictness = Strictness.LENIENT)
class BookmarkServiceImplTest {

    @Mock
    private BookmarkRepository bookmarkRepository;

    @Mock
    private VerseRepository verseRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private BookmarkServiceImpl bookmarkService;

    private Users testUser;
    private Book testBook;
    private Chapter testChapter;
    private Verse testVerse;
    private Bookmark testBookmark;

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

        // Set up test Bookmark
        testBookmark = Bookmark.builder()
                .id(1L)
                .verse(testVerse)
                .user(testUser)
                .notes("My first bookmark")
                .createdAt(LocalDateTime.now())
                .build();

        // Mock SecurityContextHolder
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(testUser);
    }

    @Test
    void createBookmark_WhenVerseExistsAndNotBookmarked_ShouldCreateBookmark() {
        // Arrange
        Long verseId = 1L;
        String notes = "My first bookmark";
        when(verseRepository.findById(verseId)).thenReturn(Optional.of(testVerse));
        when(bookmarkRepository.existsByUserAndVerse(testUser, testVerse)).thenReturn(false);
        when(bookmarkRepository.save(any(Bookmark.class))).thenReturn(testBookmark);

        // Act
        BookmarkResponse result = bookmarkService.createBookmark(verseId, notes);

        // Assert
        assertNotNull(result);
        assertEquals(testBookmark.getNotes(), result.getNotes());
        assertEquals(testVerse.getText(), result.getVerseText());
        verify(verseRepository).findById(verseId);
        verify(bookmarkRepository).existsByUserAndVerse(testUser, testVerse);
        verify(bookmarkRepository).save(any(Bookmark.class));
    }

    @Test
    void createBookmark_WhenVerseDoesNotExist_ShouldThrowException() {
        // Arrange
        Long verseId = 1L;
        String notes = "My first bookmark";
        when(verseRepository.findById(verseId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            bookmarkService.createBookmark(verseId, notes);
        });
        verify(verseRepository).findById(verseId);
        verifyNoInteractions(bookmarkRepository);
    }

    @Test
    void createBookmark_WhenVerseAlreadyBookmarked_ShouldThrowException() {
        // Arrange
        Long verseId = 1L;
        String notes = "My first bookmark";
        when(verseRepository.findById(verseId)).thenReturn(Optional.of(testVerse));
        when(bookmarkRepository.existsByUserAndVerse(testUser, testVerse)).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            bookmarkService.createBookmark(verseId, notes);
        });
        verify(verseRepository).findById(verseId);
        verify(bookmarkRepository).existsByUserAndVerse(testUser, testVerse);
        verify(bookmarkRepository, never()).save(any(Bookmark.class));
    }

    @Test
    void getBookmarksByUser_ShouldReturnUserBookmarks() {
        // Arrange
        List<Bookmark> bookmarks = Arrays.asList(testBookmark);
        when(bookmarkRepository.findByUser(testUser)).thenReturn(bookmarks);

        // Act
        List<BookmarkResponse> result = bookmarkService.getBookmarksByUser();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testBookmark.getNotes(), result.get(0).getNotes());
        verify(bookmarkRepository).findByUser(testUser);
    }

    @Test
    void getBookmarkById_WhenBookmarkExistsAndBelongsToUser_ShouldReturnBookmark() {
        // Arrange
        Long bookmarkId = 1L;
        when(bookmarkRepository.findById(bookmarkId)).thenReturn(Optional.of(testBookmark));

        // Act
        BookmarkResponse result = bookmarkService.getBookmarkById(bookmarkId);

        // Assert
        assertNotNull(result);
        assertEquals(testBookmark.getNotes(), result.getNotes());
        verify(bookmarkRepository).findById(bookmarkId);
    }

    @Test
    void getBookmarkById_WhenBookmarkDoesNotExist_ShouldThrowException() {
        // Arrange
        Long bookmarkId = 1L;
        when(bookmarkRepository.findById(bookmarkId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            bookmarkService.getBookmarkById(bookmarkId);
        });
        verify(bookmarkRepository).findById(bookmarkId);
    }

    @Test
    void getBookmarkById_WhenBookmarkDoesNotBelongToUser_ShouldThrowException() {
        // Arrange
        Long bookmarkId = 1L;
        Users otherUser = mock(Users.class);
        when(otherUser.getId()).thenReturn(2L);
        Bookmark otherBookmark = Bookmark.builder()
                .id(bookmarkId)
                .verse(testVerse)
                .user(otherUser)
                .notes("Other user's bookmark")
                .build();
        when(bookmarkRepository.findById(bookmarkId)).thenReturn(Optional.of(otherBookmark));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            bookmarkService.getBookmarkById(bookmarkId);
        });
        verify(bookmarkRepository).findById(bookmarkId);
    }

    @Test
    void updateBookmarkNotes_WhenBookmarkExistsAndBelongsToUser_ShouldUpdateNotes() {
        // Arrange
        Long bookmarkId = 1L;
        String newNotes = "Updated notes";
        when(bookmarkRepository.findById(bookmarkId)).thenReturn(Optional.of(testBookmark));
        when(bookmarkRepository.save(testBookmark)).thenReturn(testBookmark);

        // Act
        BookmarkResponse result = bookmarkService.updateBookmarkNotes(bookmarkId, newNotes);

        // Assert
        assertNotNull(result);
        assertEquals(newNotes, testBookmark.getNotes());
        verify(bookmarkRepository).findById(bookmarkId);
        verify(bookmarkRepository).save(testBookmark);
    }

    @Test
    void deleteBookmark_WhenBookmarkExistsAndBelongsToUser_ShouldDeleteBookmark() {
        // Arrange
        Long bookmarkId = 1L;
        when(bookmarkRepository.findById(bookmarkId)).thenReturn(Optional.of(testBookmark));
        doNothing().when(bookmarkRepository).delete(testBookmark);

        // Act
        bookmarkService.deleteBookmark(bookmarkId);

        // Assert
        verify(bookmarkRepository).findById(bookmarkId);
        verify(bookmarkRepository).delete(testBookmark);
    }

    @Test
    void isVerseBookmarked_WhenVerseIsBookmarked_ShouldReturnTrue() {
        // Arrange
        Long verseId = 1L;
        when(verseRepository.findById(verseId)).thenReturn(Optional.of(testVerse));
        when(bookmarkRepository.existsByUserAndVerse(testUser, testVerse)).thenReturn(true);

        // Act
        boolean result = bookmarkService.isVerseBookmarked(verseId);

        // Assert
        assertTrue(result);
        verify(verseRepository).findById(verseId);
        verify(bookmarkRepository).existsByUserAndVerse(testUser, testVerse);
    }

    @Test
    void isVerseBookmarked_WhenVerseIsNotBookmarked_ShouldReturnFalse() {
        // Arrange
        Long verseId = 1L;
        when(verseRepository.findById(verseId)).thenReturn(Optional.of(testVerse));
        when(bookmarkRepository.existsByUserAndVerse(testUser, testVerse)).thenReturn(false);

        // Act
        boolean result = bookmarkService.isVerseBookmarked(verseId);

        // Assert
        assertFalse(result);
        verify(verseRepository).findById(verseId);
        verify(bookmarkRepository).existsByUserAndVerse(testUser, testVerse);
    }
}
