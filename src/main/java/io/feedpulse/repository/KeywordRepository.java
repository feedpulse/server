package io.feedpulse.repository;

import io.feedpulse.model.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {

    Keyword findByKeyword(String keyword);

    Set<Keyword> findAllByKeywordIn(Set<String> keywords);


}
