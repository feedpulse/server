package de.feedpulse.repository;

import de.feedpulse.model.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {

    Keyword findByKeyword(String keyword);

    Set<Keyword> findAllByKeywordIn(Set<String> keywords);


}
