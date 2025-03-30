package com.brandyodhiambo.bibleApi.feature.biblemgt.service;

import com.brandyodhiambo.bibleApi.feature.biblemgt.models.Bible;
import com.brandyodhiambo.bibleApi.feature.biblemgt.models.Book;
import com.brandyodhiambo.bibleApi.feature.biblemgt.models.Chapter;
import com.brandyodhiambo.bibleApi.feature.biblemgt.models.Verse;
import com.brandyodhiambo.bibleApi.feature.biblemgt.models.dto.BibleResponse;
import com.brandyodhiambo.bibleApi.feature.biblemgt.models.dto.BookResponse;
import com.brandyodhiambo.bibleApi.feature.biblemgt.models.dto.ChapterResponse;
import com.brandyodhiambo.bibleApi.feature.biblemgt.models.dto.VerseResponse;
import com.brandyodhiambo.bibleApi.feature.biblemgt.repository.BibleRepository;
import com.brandyodhiambo.bibleApi.feature.biblemgt.repository.BookRepository;
import com.brandyodhiambo.bibleApi.feature.biblemgt.repository.ChapterRepository;
import com.brandyodhiambo.bibleApi.feature.biblemgt.repository.VerseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BibleServiceImpl implements BibleService {

    private final BibleRepository bibleRepository;
    private final BookRepository bookRepository;
    private final ChapterRepository chapterRepository;
    private final VerseRepository verseRepository;

    @Override
    public List<BibleResponse> getAllBibles() {
        return bibleRepository.findAll().stream()
                .map(this::mapToBibleResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BibleResponse getBibleById(Long id) {
        Bible bible = bibleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Bible not found with id: " + id));
        return mapToBibleResponse(bible);
    }

    @Override
    public BibleResponse getBibleByName(String name) {
        Bible bible = bibleRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Bible not found with name: " + name));
        return mapToBibleResponse(bible);
    }

    @Override
    public BibleResponse getBibleByAbbreviation(String abbreviation) {
        Bible bible = bibleRepository.findByAbbreviation(abbreviation)
                .orElseThrow(() -> new EntityNotFoundException("Bible not found with abbreviation: " + abbreviation));
        return mapToBibleResponse(bible);
    }

    @Override
    public List<BookResponse> getAllBooks(Long bibleId) {
        Bible bible = bibleRepository.findById(bibleId)
                .orElseThrow(() -> new EntityNotFoundException("Bible not found with id: " + bibleId));
        return bookRepository.findByBible(bible).stream()
                .map(this::mapToBookResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BookResponse getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + id));
        return mapToBookResponse(book);
    }

    @Override
    public BookResponse getBookByName(Long bibleId, String name) {
        Bible bible = bibleRepository.findById(bibleId)
                .orElseThrow(() -> new EntityNotFoundException("Bible not found with id: " + bibleId));
        Book book = bookRepository.findByBibleAndName(bible, name)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with name: " + name));
        return mapToBookResponse(book);
    }

    @Override
    public BookResponse getBookByAbbreviation(Long bibleId, String abbreviation) {
        Bible bible = bibleRepository.findById(bibleId)
                .orElseThrow(() -> new EntityNotFoundException("Bible not found with id: " + bibleId));
        Book book = bookRepository.findByBibleAndAbbreviation(bible, abbreviation)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with abbreviation: " + abbreviation));
        return mapToBookResponse(book);
    }

    @Override
    public List<ChapterResponse> getAllChapters(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + bookId));
        return chapterRepository.findByBook(book).stream()
                .map(this::mapToChapterResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ChapterResponse getChapterById(Long id) {
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Chapter not found with id: " + id));
        return mapToChapterResponse(chapter);
    }

    @Override
    public ChapterResponse getChapterByNumber(Long bookId, Integer number) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + bookId));
        Chapter chapter = chapterRepository.findByBookAndNumber(book, number)
                .orElseThrow(() -> new EntityNotFoundException("Chapter not found with number: " + number));
        return mapToChapterResponse(chapter);
    }

    @Override
    public List<VerseResponse> getAllVerses(Long chapterId) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new EntityNotFoundException("Chapter not found with id: " + chapterId));
        return verseRepository.findByChapter(chapter).stream()
                .map(this::mapToVerseResponse)
                .collect(Collectors.toList());
    }

    @Override
    public VerseResponse getVerseById(Long id) {
        Verse verse = verseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Verse not found with id: " + id));
        return mapToVerseResponse(verse);
    }

    @Override
    public VerseResponse getVerseByNumber(Long chapterId, Integer number) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new EntityNotFoundException("Chapter not found with id: " + chapterId));
        Verse verse = verseRepository.findByChapterAndNumber(chapter, number)
                .orElseThrow(() -> new EntityNotFoundException("Verse not found with number: " + number));
        return mapToVerseResponse(verse);
    }

    @Override
    public VerseResponse getVerseByReference(String bookName, Integer chapterNumber, Integer verseNumber) {
        Verse verse = verseRepository.findByReference(bookName, chapterNumber, verseNumber)
                .orElseThrow(() -> new EntityNotFoundException("Verse not found with reference: " + bookName + " " + chapterNumber + ":" + verseNumber));
        return mapToVerseResponse(verse);
    }

    @Override
    public List<VerseResponse> searchVersesByKeyword(String keyword) {
        return verseRepository.searchByKeyword(keyword).stream()
                .map(this::mapToVerseResponse)
                .collect(Collectors.toList());
    }

    private BibleResponse mapToBibleResponse(Bible bible) {
        return BibleResponse.builder()
                .id(bible.getId())
                .name(bible.getName())
                .abbreviation(bible.getAbbreviation())
                .description(bible.getDescription())
                .language(bible.getLanguage())
                .createdAt(bible.getCreatedAt())
                .build();
    }

    private BookResponse mapToBookResponse(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .name(book.getName())
                .abbreviation(book.getAbbreviation())
                .position(book.getPosition())
                .description(book.getDescription())
                .bibleId(book.getBible().getId())
                .bibleName(book.getBible().getName())
                .createdAt(book.getCreatedAt())
                .build();
    }

    private ChapterResponse mapToChapterResponse(Chapter chapter) {
        return ChapterResponse.builder()
                .id(chapter.getId())
                .number(chapter.getNumber())
                .bookId(chapter.getBook().getId())
                .bookName(chapter.getBook().getName())
                .createdAt(chapter.getCreatedAt())
                .build();
    }

    private VerseResponse mapToVerseResponse(Verse verse) {
        return VerseResponse.builder()
                .id(verse.getId())
                .number(verse.getNumber())
                .text(verse.getText())
                .chapterId(verse.getChapter().getId())
                .chapterNumber(verse.getChapter().getNumber())
                .bookId(verse.getChapter().getBook().getId())
                .bookName(verse.getChapter().getBook().getName())
                .createdAt(verse.getCreatedAt())
                .build();
    }
}