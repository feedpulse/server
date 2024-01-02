package dev.feder.repository;

import dev.feder.model.Entry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EntryRepository extends JpaRepository<Entry, UUID> {

    Optional<Entry> findByLink(String link);
    Page<Entry> findAllByFeedUuid(UUID feedUuid, Pageable pageable);
}
