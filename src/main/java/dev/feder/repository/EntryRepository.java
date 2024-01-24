package dev.feder.repository;

import dev.feder.model.Entry;
import io.micrometer.observation.ObservationFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface EntryRepository extends JpaRepository<Entry, UUID> {

    Optional<Entry> findByLink(String link);
    Page<Entry> findAllByFeedUuid(UUID feedUuid, Pageable pageable);

    @Query("SELECT e FROM Entry e JOIN e.feed f JOIN f.users u WHERE u.id = :userId AND f.uuid = :feedUuid")
    Page<Entry> findEntriesByFeedUuidAndUsersId(UUID feedUuid, Long userId, Pageable pageable);

    @Query("SELECT e FROM Entry e JOIN e.feed f JOIN f.users u WHERE u.id = :userId")
    Page<Entry> findEntriesByUsersId(Long userId, Pageable pageable);

    @Query("SELECT e FROM Entry e JOIN e.feed f JOIN f.users u WHERE u.id = :userId AND e.uuid = :entryUuid")
    Optional<Entry> findEntryByUuidAndUsersId(UUID entryUuid, Long userId);

    @Query("SELECT e FROM Entry e JOIN e.feed f JOIN f.users u WHERE u.id = :userId AND e.link = :link")
    Optional<Entry> findEntryByLinkAndUsersId(String link, Long userId);

    @Query("SELECT e FROM Entry e JOIN e.userEntryInteractions uei JOIN uei.user u WHERE u.id = :id AND uei.favorite = true")
    Page<Entry> findFavoriteEntriesByUsersId(Long id, Pageable pageable);

    @Query("SELECT e FROM Entry e JOIN e.userEntryInteractions uei JOIN uei.user u WHERE u.id = :id AND uei.bookmark = true")
    Page<Entry> findBookmarkedEntriesByUsersId(Long id, Pageable pageable);
}
