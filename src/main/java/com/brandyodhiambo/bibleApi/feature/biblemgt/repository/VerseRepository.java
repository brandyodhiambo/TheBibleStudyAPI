package com.brandyodhiambo.bibleApi.feature.biblemgt.repository;

import com.brandyodhiambo.bibleApi.feature.biblemgt.models.Chapter;
import com.brandyodhiambo.bibleApi.feature.biblemgt.models.Verse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VerseRepository extends JpaRepository<Verse, Long> {
    List<Verse> findByChapter(Chapter chapter);
    List<Verse> findByChapterId(Long chapterId);
    Optional<Verse> findByChapterAndNumber(Chapter chapter, Integer number);
    Optional<Verse> findByChapterIdAndNumber(Long chapterId, Integer number);
    
    @Query("SELECT v FROM Verse v WHERE v.text LIKE %:keyword%")
    List<Verse> searchByKeyword(@Param("keyword") String keyword);
    
    @Query("SELECT v FROM Verse v JOIN v.chapter c JOIN c.book b WHERE b.name = :bookName AND c.number = :chapterNumber AND v.number = :verseNumber")
    Optional<Verse> findByReference(@Param("bookName") String bookName, @Param("chapterNumber") Integer chapterNumber, @Param("verseNumber") Integer verseNumber);
}