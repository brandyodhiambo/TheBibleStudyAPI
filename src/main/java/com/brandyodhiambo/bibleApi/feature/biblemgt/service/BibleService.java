package com.brandyodhiambo.bibleApi.feature.biblemgt.service;

import com.brandyodhiambo.bibleApi.feature.biblemgt.models.dto.BibleResponse;
import com.brandyodhiambo.bibleApi.feature.biblemgt.models.dto.BookResponse;
import com.brandyodhiambo.bibleApi.feature.biblemgt.models.dto.ChapterResponse;
import com.brandyodhiambo.bibleApi.feature.biblemgt.models.dto.VerseResponse;

import java.util.List;

public interface BibleService {
    // Bible operations
    List<BibleResponse> getAllBibles();
    BibleResponse getBibleById(Long id);
    BibleResponse getBibleByName(String name);
    BibleResponse getBibleByAbbreviation(String abbreviation);
    
    // Book operations
    List<BookResponse> getAllBooks(Long bibleId);
    BookResponse getBookById(Long id);
    BookResponse getBookByName(Long bibleId, String name);
    BookResponse getBookByAbbreviation(Long bibleId, String abbreviation);
    
    // Chapter operations
    List<ChapterResponse> getAllChapters(Long bookId);
    ChapterResponse getChapterById(Long id);
    ChapterResponse getChapterByNumber(Long bookId, Integer number);
    
    // Verse operations
    List<VerseResponse> getAllVerses(Long chapterId);
    VerseResponse getVerseById(Long id);
    VerseResponse getVerseByNumber(Long chapterId, Integer number);
    VerseResponse getVerseByReference(String bookName, Integer chapterNumber, Integer verseNumber);
    
    // Search operations
    List<VerseResponse> searchVersesByKeyword(String keyword);
}