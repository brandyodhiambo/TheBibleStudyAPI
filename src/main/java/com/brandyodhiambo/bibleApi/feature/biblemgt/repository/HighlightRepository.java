package com.brandyodhiambo.bibleApi.feature.biblemgt.repository;

import com.brandyodhiambo.bibleApi.feature.biblemgt.models.Highlight;
import com.brandyodhiambo.bibleApi.feature.biblemgt.models.Verse;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HighlightRepository extends JpaRepository<Highlight, Long> {
    List<Highlight> findByUser(Users user);
    List<Highlight> findByUserId(Long userId);
    List<Highlight> findByVerse(Verse verse);
    List<Highlight> findByVerseId(Long verseId);
    Optional<Highlight> findByUserAndVerse(Users user, Verse verse);
    Optional<Highlight> findByUserIdAndVerseId(Long userId, Long verseId);
    boolean existsByUserAndVerse(Users user, Verse verse);
    boolean existsByUserIdAndVerseId(Long userId, Long verseId);
}