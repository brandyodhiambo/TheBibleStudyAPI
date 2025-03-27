package com.brandyodhiambo.bibleApi.feature.biblemgt.repository;

import com.brandyodhiambo.bibleApi.feature.biblemgt.models.Bible;
import com.brandyodhiambo.bibleApi.feature.biblemgt.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByBible(Bible bible);
    List<Book> findByBibleId(Long bibleId);
    Optional<Book> findByBibleAndName(Bible bible, String name);
    Optional<Book> findByBibleIdAndName(Long bibleId, String name);
    Optional<Book> findByBibleAndAbbreviation(Bible bible, String abbreviation);
    Optional<Book> findByBibleIdAndAbbreviation(Long bibleId, String abbreviation);
}