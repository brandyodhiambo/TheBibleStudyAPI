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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BibleServiceImplTest {

    @Mock
    private BibleRepository bibleRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ChapterRepository chapterRepository;

    @Mock
    private VerseRepository verseRepository;

    @InjectMocks
    private BibleServiceImpl bibleService;

    private Bible testBible;
    private Book testBook;
    private Chapter testChapter;
    private Verse testVerse;

    @BeforeEach
    void setUp() {
        // Set up test Bible
        testBible = Bible.builder()
                .id(1L)
                .name("King James Version")
                .abbreviation("KJV")
                .description("The King James Version of the Bible")
                .language("English")
                .createdAt(LocalDateTime.now())
                .build();

        // Set up test Book
        testBook = Book.builder()
                .id(1L)
                .name("Genesis")
                .abbreviation("Gen")
                .position(1)
                .description("The first book of the Bible")
                .bible(testBible)
                .createdAt(LocalDateTime.now())
                .build();

        // Set up test Chapter
        testChapter = Chapter.builder()
                .id(1L)
                .number(1)
                .book(testBook)
                .createdAt(LocalDateTime.now())
                .build();

        // Set up test Verse
        testVerse = Verse.builder()
                .id(1L)
                .number(1)
                .text("In the beginning God created the heaven and the earth.")
                .chapter(testChapter)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void getAllBibles_ShouldReturnAllBibles() {
        // Arrange
        List<Bible> bibles = Arrays.asList(testBible);
        when(bibleRepository.findAll()).thenReturn(bibles);

        // Act
        List<BibleResponse> result = bibleService.getAllBibles();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testBible.getName(), result.get(0).getName());
        verify(bibleRepository).findAll();
    }

    @Test
    void getBibleById_WhenBibleExists_ShouldReturnBible() {
        // Arrange
        when(bibleRepository.findById(1L)).thenReturn(Optional.of(testBible));

        // Act
        BibleResponse result = bibleService.getBibleById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testBible.getName(), result.getName());
        verify(bibleRepository).findById(1L);
    }

    @Test
    void getBibleById_WhenBibleDoesNotExist_ShouldThrowException() {
        // Arrange
        when(bibleRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            bibleService.getBibleById(1L);
        });
        verify(bibleRepository).findById(1L);
    }

    @Test
    void getBibleByName_WhenBibleExists_ShouldReturnBible() {
        // Arrange
        String bibleName = "King James Version";
        when(bibleRepository.findByName(bibleName)).thenReturn(Optional.of(testBible));

        // Act
        BibleResponse result = bibleService.getBibleByName(bibleName);

        // Assert
        assertNotNull(result);
        assertEquals(testBible.getName(), result.getName());
        verify(bibleRepository).findByName(bibleName);
    }

    @Test
    void getBibleByAbbreviation_WhenBibleExists_ShouldReturnBible() {
        // Arrange
        String abbreviation = "KJV";
        when(bibleRepository.findByAbbreviation(abbreviation)).thenReturn(Optional.of(testBible));

        // Act
        BibleResponse result = bibleService.getBibleByAbbreviation(abbreviation);

        // Assert
        assertNotNull(result);
        assertEquals(testBible.getAbbreviation(), result.getAbbreviation());
        verify(bibleRepository).findByAbbreviation(abbreviation);
    }

    @Test
    void getAllBooks_WhenBibleExists_ShouldReturnAllBooks() {
        // Arrange
        List<Book> books = Arrays.asList(testBook);
        when(bibleRepository.findById(1L)).thenReturn(Optional.of(testBible));
        when(bookRepository.findByBible(testBible)).thenReturn(books);

        // Act
        List<BookResponse> result = bibleService.getAllBooks(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testBook.getName(), result.get(0).getName());
        verify(bibleRepository).findById(1L);
        verify(bookRepository).findByBible(testBible);
    }

    @Test
    void getBookById_WhenBookExists_ShouldReturnBook() {
        // Arrange
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        // Act
        BookResponse result = bibleService.getBookById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testBook.getName(), result.getName());
        verify(bookRepository).findById(1L);
    }

    @Test
    void getAllChapters_WhenBookExists_ShouldReturnAllChapters() {
        // Arrange
        List<Chapter> chapters = Arrays.asList(testChapter);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(chapterRepository.findByBook(testBook)).thenReturn(chapters);

        // Act
        List<ChapterResponse> result = bibleService.getAllChapters(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testChapter.getNumber(), result.get(0).getNumber());
        verify(bookRepository).findById(1L);
        verify(chapterRepository).findByBook(testBook);
    }

    @Test
    void getChapterById_WhenChapterExists_ShouldReturnChapter() {
        // Arrange
        when(chapterRepository.findById(1L)).thenReturn(Optional.of(testChapter));

        // Act
        ChapterResponse result = bibleService.getChapterById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testChapter.getNumber(), result.getNumber());
        verify(chapterRepository).findById(1L);
    }

    @Test
    void getAllVerses_WhenChapterExists_ShouldReturnAllVerses() {
        // Arrange
        List<Verse> verses = Arrays.asList(testVerse);
        when(chapterRepository.findById(1L)).thenReturn(Optional.of(testChapter));
        when(verseRepository.findByChapter(testChapter)).thenReturn(verses);

        // Act
        List<VerseResponse> result = bibleService.getAllVerses(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testVerse.getText(), result.get(0).getText());
        verify(chapterRepository).findById(1L);
        verify(verseRepository).findByChapter(testChapter);
    }

    @Test
    void getVerseById_WhenVerseExists_ShouldReturnVerse() {
        // Arrange
        when(verseRepository.findById(1L)).thenReturn(Optional.of(testVerse));

        // Act
        VerseResponse result = bibleService.getVerseById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testVerse.getText(), result.getText());
        verify(verseRepository).findById(1L);
    }

    @Test
    void searchVersesByKeyword_ShouldReturnMatchingVerses() {
        // Arrange
        String keyword = "beginning";
        List<Verse> verses = Arrays.asList(testVerse);
        when(verseRepository.searchByKeyword(keyword)).thenReturn(verses);

        // Act
        List<VerseResponse> result = bibleService.searchVersesByKeyword(keyword);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testVerse.getText(), result.get(0).getText());
        verify(verseRepository).searchByKeyword(keyword);
    }
}