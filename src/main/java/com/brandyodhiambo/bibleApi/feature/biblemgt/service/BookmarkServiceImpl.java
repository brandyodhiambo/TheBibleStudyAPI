package com.brandyodhiambo.bibleApi.feature.biblemgt.service;

import com.brandyodhiambo.bibleApi.feature.biblemgt.models.Bookmark;
import com.brandyodhiambo.bibleApi.feature.biblemgt.models.Verse;
import com.brandyodhiambo.bibleApi.feature.biblemgt.models.dto.BookmarkResponse;
import com.brandyodhiambo.bibleApi.feature.biblemgt.repository.BookmarkRepository;
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
public class BookmarkServiceImpl implements BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final VerseRepository verseRepository;

    @Override
    public BookmarkResponse createBookmark(Long verseId, String notes) {
        Users currentUser = getCurrentUser();
        
        Verse verse = verseRepository.findById(verseId)
                .orElseThrow(() -> new EntityNotFoundException("Verse not found with id: " + verseId));
        
        // Check if bookmark already exists
        if (bookmarkRepository.existsByUserAndVerse(currentUser, verse)) {
            throw new IllegalStateException("Verse is already bookmarked");
        }
        
        Bookmark bookmark = Bookmark.builder()
                .verse(verse)
                .user(currentUser)
                .notes(notes)
                .build();
        
        Bookmark savedBookmark = bookmarkRepository.save(bookmark);
        return mapToBookmarkResponse(savedBookmark);
    }

    @Override
    public List<BookmarkResponse> getAllBookmarks() {
        return bookmarkRepository.findAll().stream()
                .map(this::mapToBookmarkResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookmarkResponse> getBookmarksByUser() {
        Users currentUser = getCurrentUser();
        return bookmarkRepository.findByUser(currentUser).stream()
                .map(this::mapToBookmarkResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BookmarkResponse getBookmarkById(Long id) {
        Users currentUser = getCurrentUser();
        
        Bookmark bookmark = bookmarkRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Bookmark not found with id: " + id));
        
        // Check if the bookmark belongs to the current user
        if (!bookmark.getUser().equals(currentUser)) {
            throw new IllegalStateException("You can only access your own bookmarks");
        }
        
        return mapToBookmarkResponse(bookmark);
    }

    @Override
    public BookmarkResponse getBookmarkByVerseId(Long verseId) {
        Users currentUser = getCurrentUser();
        
        Verse verse = verseRepository.findById(verseId)
                .orElseThrow(() -> new EntityNotFoundException("Verse not found with id: " + verseId));
        
        Bookmark bookmark = bookmarkRepository.findByUserAndVerse(currentUser, verse)
                .orElseThrow(() -> new EntityNotFoundException("Bookmark not found for verse id: " + verseId));
        
        return mapToBookmarkResponse(bookmark);
    }

    @Override
    public BookmarkResponse updateBookmarkNotes(Long id, String notes) {
        Users currentUser = getCurrentUser();
        
        Bookmark bookmark = bookmarkRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Bookmark not found with id: " + id));
        
        // Check if the bookmark belongs to the current user
        if (!bookmark.getUser().equals(currentUser)) {
            throw new IllegalStateException("You can only update your own bookmarks");
        }
        
        bookmark.setNotes(notes);
        Bookmark updatedBookmark = bookmarkRepository.save(bookmark);
        
        return mapToBookmarkResponse(updatedBookmark);
    }

    @Override
    public void deleteBookmark(Long id) {
        Users currentUser = getCurrentUser();
        
        Bookmark bookmark = bookmarkRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Bookmark not found with id: " + id));
        
        // Check if the bookmark belongs to the current user
        if (!bookmark.getUser().equals(currentUser)) {
            throw new IllegalStateException("You can only delete your own bookmarks");
        }
        
        bookmarkRepository.delete(bookmark);
    }

    @Override
    public boolean isVerseBookmarked(Long verseId) {
        Users currentUser = getCurrentUser();
        
        Verse verse = verseRepository.findById(verseId)
                .orElseThrow(() -> new EntityNotFoundException("Verse not found with id: " + verseId));
        
        return bookmarkRepository.existsByUserAndVerse(currentUser, verse);
    }
    
    private Users getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (Users) authentication.getPrincipal();
    }
    
    private BookmarkResponse mapToBookmarkResponse(Bookmark bookmark) {
        Verse verse = bookmark.getVerse();
        
        return BookmarkResponse.builder()
                .id(bookmark.getId())
                .verseId(verse.getId())
                .verseNumber(verse.getNumber())
                .verseText(verse.getText())
                .chapterId(verse.getChapter().getId())
                .chapterNumber(verse.getChapter().getNumber())
                .bookId(verse.getChapter().getBook().getId())
                .bookName(verse.getChapter().getBook().getName())
                .userId(bookmark.getUser().getId())
                .username(bookmark.getUser().getUsername())
                .notes(bookmark.getNotes())
                .createdAt(bookmark.getCreatedAt())
                .build();
    }
}