package com.brandyodhiambo.bibleApi.feature.biblemgt.repository;

import com.brandyodhiambo.bibleApi.feature.biblemgt.models.Book;
import com.brandyodhiambo.bibleApi.feature.biblemgt.models.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    List<Chapter> findByBook(Book book);
    List<Chapter> findByBookId(Long bookId);
    Optional<Chapter> findByBookAndNumber(Book book, Integer number);
    Optional<Chapter> findByBookIdAndNumber(Long bookId, Integer number);
}