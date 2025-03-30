package com.brandyodhiambo.bibleApi.feature.biblemgt.controller;

import com.brandyodhiambo.bibleApi.feature.biblemgt.models.dto.BibleResponse;
import com.brandyodhiambo.bibleApi.feature.biblemgt.models.dto.BookResponse;
import com.brandyodhiambo.bibleApi.feature.biblemgt.models.dto.ChapterResponse;
import com.brandyodhiambo.bibleApi.feature.biblemgt.models.dto.VerseResponse;
import com.brandyodhiambo.bibleApi.feature.biblemgt.service.BibleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bibles")
@RequiredArgsConstructor
public class BibleController {

    private final BibleService bibleService;

    // Bible endpoints
    @GetMapping
    public ResponseEntity<List<BibleResponse>> getAllBibles() {
        return ResponseEntity.ok(bibleService.getAllBibles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BibleResponse> getBibleById(@PathVariable Long id) {
        return ResponseEntity.ok(bibleService.getBibleById(id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<BibleResponse> getBibleByName(@PathVariable String name) {
        return ResponseEntity.ok(bibleService.getBibleByName(name));
    }

    @GetMapping("/abbreviation/{abbreviation}")
    public ResponseEntity<BibleResponse> getBibleByAbbreviation(@PathVariable String abbreviation) {
        return ResponseEntity.ok(bibleService.getBibleByAbbreviation(abbreviation));
    }

    // Book endpoints
    @GetMapping("/{bibleId}/books")
    public ResponseEntity<List<BookResponse>> getAllBooks(@PathVariable Long bibleId) {
        return ResponseEntity.ok(bibleService.getAllBooks(bibleId));
    }

    @GetMapping("/books/{id}")
    public ResponseEntity<BookResponse> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bibleService.getBookById(id));
    }

    @GetMapping("/{bibleId}/books/name/{name}")
    public ResponseEntity<BookResponse> getBookByName(@PathVariable Long bibleId, @PathVariable String name) {
        return ResponseEntity.ok(bibleService.getBookByName(bibleId, name));
    }

    @GetMapping("/{bibleId}/books/abbreviation/{abbreviation}")
    public ResponseEntity<BookResponse> getBookByAbbreviation(@PathVariable Long bibleId, @PathVariable String abbreviation) {
        return ResponseEntity.ok(bibleService.getBookByAbbreviation(bibleId, abbreviation));
    }

    // Chapter endpoints
    @GetMapping("/books/{bookId}/chapters")
    public ResponseEntity<List<ChapterResponse>> getAllChapters(@PathVariable Long bookId) {
        return ResponseEntity.ok(bibleService.getAllChapters(bookId));
    }

    @GetMapping("/chapters/{id}")
    public ResponseEntity<ChapterResponse> getChapterById(@PathVariable Long id) {
        return ResponseEntity.ok(bibleService.getChapterById(id));
    }

    @GetMapping("/books/{bookId}/chapters/{number}")
    public ResponseEntity<ChapterResponse> getChapterByNumber(@PathVariable Long bookId, @PathVariable Integer number) {
        return ResponseEntity.ok(bibleService.getChapterByNumber(bookId, number));
    }

    // Verse endpoints
    @GetMapping("/chapters/{chapterId}/verses")
    public ResponseEntity<List<VerseResponse>> getAllVerses(@PathVariable Long chapterId) {
        return ResponseEntity.ok(bibleService.getAllVerses(chapterId));
    }

    @GetMapping("/verses/{id}")
    public ResponseEntity<VerseResponse> getVerseById(@PathVariable Long id) {
        return ResponseEntity.ok(bibleService.getVerseById(id));
    }

    @GetMapping("/chapters/{chapterId}/verses/{number}")
    public ResponseEntity<VerseResponse> getVerseByNumber(@PathVariable Long chapterId, @PathVariable Integer number) {
        return ResponseEntity.ok(bibleService.getVerseByNumber(chapterId, number));
    }

    @GetMapping("/verses/reference")
    public ResponseEntity<VerseResponse> getVerseByReference(
            @RequestParam String bookName,
            @RequestParam Integer chapterNumber,
            @RequestParam Integer verseNumber) {
        return ResponseEntity.ok(bibleService.getVerseByReference(bookName, chapterNumber, verseNumber));
    }

    // Search endpoint
    @GetMapping("/search")
    public ResponseEntity<List<VerseResponse>> searchVersesByKeyword(@RequestParam String keyword) {
        return ResponseEntity.ok(bibleService.searchVersesByKeyword(keyword));
    }
}