package com.brandyodhiambo.bibleApi.feature.biblemgt.repository;

import com.brandyodhiambo.bibleApi.feature.biblemgt.models.Bible;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BibleRepository extends JpaRepository<Bible, Long> {
    Optional<Bible> findByName(String name);
    Optional<Bible> findByAbbreviation(String abbreviation);
}