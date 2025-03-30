package com.brandyodhiambo.bibleApi.feature.biblemgt.controller;

import com.brandyodhiambo.bibleApi.feature.biblemgt.models.dto.BookmarkResponse;
import com.brandyodhiambo.bibleApi.feature.biblemgt.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @PostMapping("/verses/{verseId}")
    public ResponseEntity<BookmarkResponse> createBookmark(
            @PathVariable Long verseId,
            @RequestBody(required = false) Map<String, String> payload) {
        String notes = payload != null ? payload.get("notes") : null;
        return ResponseEntity.status(HttpStatus.CREATED).body(bookmarkService.createBookmark(verseId, notes));
    }

    @GetMapping
    public ResponseEntity<List<BookmarkResponse>> getBookmarksByUser() {
        return ResponseEntity.ok(bookmarkService.getBookmarksByUser());
    }

    @GetMapping("/all")
    public ResponseEntity<List<BookmarkResponse>> getAllBookmarks() {
        return ResponseEntity.ok(bookmarkService.getAllBookmarks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookmarkResponse> getBookmarkById(@PathVariable Long id) {
        return ResponseEntity.ok(bookmarkService.getBookmarkById(id));
    }

    @GetMapping("/verses/{verseId}")
    public ResponseEntity<BookmarkResponse> getBookmarkByVerseId(@PathVariable Long verseId) {
        return ResponseEntity.ok(bookmarkService.getBookmarkByVerseId(verseId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookmarkResponse> updateBookmarkNotes(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {
        String notes = payload.get("notes");
        return ResponseEntity.ok(bookmarkService.updateBookmarkNotes(id, notes));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBookmark(@PathVariable Long id) {
        bookmarkService.deleteBookmark(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/verses/{verseId}/check")
    public ResponseEntity<Map<String, Boolean>> isVerseBookmarked(@PathVariable Long verseId) {
        boolean isBookmarked = bookmarkService.isVerseBookmarked(verseId);
        return ResponseEntity.ok(Map.of("bookmarked", isBookmarked));
    }
}