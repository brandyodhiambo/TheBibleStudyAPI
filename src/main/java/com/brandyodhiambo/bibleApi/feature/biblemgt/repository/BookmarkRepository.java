package com.brandyodhiambo.bibleApi.feature.biblemgt.repository;

import com.brandyodhiambo.bibleApi.feature.biblemgt.models.Bookmark;
import com.brandyodhiambo.bibleApi.feature.biblemgt.models.Verse;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    List<Bookmark> findByUser(Users user);
    List<Bookmark> findByUserId(Long userId);
    List<Bookmark> findByVerse(Verse verse);
    List<Bookmark> findByVerseId(Long verseId);
    Optional<Bookmark> findByUserAndVerse(Users user, Verse verse);
    Optional<Bookmark> findByUserIdAndVerseId(Long userId, Long verseId);
    boolean existsByUserAndVerse(Users user, Verse verse);
    boolean existsByUserIdAndVerseId(Long userId, Long verseId);
}