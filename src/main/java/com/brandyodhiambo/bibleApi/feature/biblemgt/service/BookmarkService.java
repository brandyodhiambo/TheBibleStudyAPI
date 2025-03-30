package com.brandyodhiambo.bibleApi.feature.biblemgt.service;

import com.brandyodhiambo.bibleApi.feature.biblemgt.models.dto.BookmarkResponse;

import java.util.List;

public interface BookmarkService {
    // Create a bookmark
    BookmarkResponse createBookmark(Long verseId, String notes);
    
    // Get bookmarks
    List<BookmarkResponse> getAllBookmarks();
    List<BookmarkResponse> getBookmarksByUser();
    BookmarkResponse getBookmarkById(Long id);
    BookmarkResponse getBookmarkByVerseId(Long verseId);
    
    // Update a bookmark
    BookmarkResponse updateBookmarkNotes(Long id, String notes);
    
    // Delete a bookmark
    void deleteBookmark(Long id);
    
    // Check if a verse is bookmarked by the current user
    boolean isVerseBookmarked(Long verseId);
}