package dev.feder.repository;

import dev.feder.model.Keyword;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {

    Keyword findByKeyword(String keyword);

    Set<Keyword> findAllByKeywordIn(Set<String> keywords);


}
