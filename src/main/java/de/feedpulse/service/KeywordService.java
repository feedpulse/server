package de.feedpulse.service;

import de.feedpulse.model.Keyword;
import de.feedpulse.repository.KeywordRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class KeywordService {

    @PersistenceContext
    private EntityManager entityManager;
    private final KeywordRepository keywordRepository;

    public KeywordService(KeywordRepository keywordRepository) {
        this.keywordRepository = keywordRepository;
    }

    public Keyword getKeyword(String keyword) {
        return keywordRepository.findByKeyword(keyword);
    }

    public Keyword saveKeyword(Keyword keyword) {
        return keywordRepository.save(keyword);
    }

    @Transactional
    public Set<Keyword> addMissingKeywords(Set<String> keywords) {
        Set<Keyword> keywordList = new HashSet<>();
        for (String keywordStr : keywords) {
            Keyword keyword = keywordRepository.findByKeyword(keywordStr);
            if (keyword == null) {
                // keyword does not exist in database, save it
                Keyword newKeyword = new Keyword(keywordStr);
                keywordRepository.save(newKeyword);
                keywordList.add(newKeyword);
            } else {
                // keyword already exists in database
                keywordList.add(keyword);
            }
        }

        return keywordList;
    }


}
