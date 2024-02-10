package io.feedpulse.repository;

import io.feedpulse.model.Entry;
import io.feedpulse.model.Feed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public interface EntryRepository extends JpaRepository<Entry, UUID> {

    Optional<Entry> findByLink(String link);

    Page<Entry> findAllByFeedUuid(UUID feedUuid, Pageable pageable);

    @Query("SELECT e.uuid FROM Entry e WHERE e.feed = :feed")
    List<UUID> findUuidsByFeedId(Feed feed);

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

    @Query("SELECT e FROM Entry e JOIN e.feed f JOIN f.users u WHERE u.id = :id AND (e.title LIKE %:searchString% OR e.text LIKE %:searchString%)")
    Page<Entry> searchEntriesByUsersId(Long id, String searchString, Pageable pageable);

    @Query("SELECT e FROM Entry e JOIN e.feed f JOIN f.users u WHERE u.id = :userId AND f.uuid = :feedUuid AND (e.title LIKE %:searchString% OR e.text LIKE %:searchString%)")
    Page<Entry> searchEntriesByFeedUuidAndUsersId(UUID feedUuid, Long userId, String searchString, Pageable pageable);

    @Query("SELECT e FROM Entry e JOIN e.userEntryInteractions uei JOIN uei.user u WHERE u.id = :userId AND (e.title LIKE %:searchString% OR e.text LIKE %:searchString%)")
    Page<Entry> searchBookmarkedEntriesByUsersId(Long userId, String searchString, Pageable pageRequest);

    @Query("SELECT e FROM Entry e JOIN e.userEntryInteractions uei JOIN uei.user u WHERE u.id = :userId AND (e.title LIKE %:searchString% OR e.text LIKE %:searchString%)")
    Page<Entry> searchFavoriteEntriesByUsersId(Long userId, String searchString, Pageable pageRequest);
}
