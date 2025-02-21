package de.feedpulse.repository;

import de.feedpulse.model.Entry;
import de.feedpulse.model.Feed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EntryRepository extends JpaRepository<Entry, UUID> {

    Optional<Entry> findByLink(String link);

    Page<Entry> findAllByFeedUuid(UUID feedUuid, Pageable pageable);

    @Query("SELECT e.uuid FROM Entry e WHERE e.feed = :feed")
    List<UUID> findUuidsByFeedId(Feed feed);

    @Query("SELECT e FROM Entry e JOIN e.feed f JOIN f.users u WHERE u.id = :userId AND f.uuid = :feedUuid")
    Page<Entry> findEntriesByFeedUuidAndUsersId(UUID feedUuid, Long userId, Pageable pageable);

    @Query("SELECT e FROM Entry e JOIN e.feed f JOIN f.users u WHERE u.uuid = :userUuid AND f.uuid = :feedUuid")
    Page<Entry> findEntriesByFeedUuidAndUsersUuid(UUID feedUuid, UUID userUuid, Pageable pageable);

    @Query("SELECT e FROM Entry e JOIN e.feed f JOIN f.users u LEFT JOIN e.userEntryInteractions uei ON uei.user.uuid = :userUuid WHERE u.uuid = :userUuid AND f.uuid = :feedUuid AND (uei.read IS NULL OR uei.read = false)")
    Page<Entry> findUnreadEntriesByFeedUuidAndUsersUuid(UUID feedUuid, UUID userUuid, Pageable pageable);

    @Query("SELECT e FROM Entry e JOIN e.feed f JOIN f.users u WHERE u.id = :userId")
    Page<Entry> findEntriesByUsersId(Long userId, Pageable pageable);

    @Query("SELECT e FROM Entry e JOIN e.feed f JOIN f.users u WHERE u.uuid = :userUuid")
    Page<Entry> findEntriesByUsersUuid(UUID userUuid, Pageable pageable);

    @Query("SELECT e FROM Entry e JOIN e.feed f JOIN f.users u LEFT JOIN e.userEntryInteractions uei ON uei.user.uuid = :userUuid WHERE u.uuid = :userUuid AND (uei.read IS NULL OR uei.read = false)")
    Page<Entry> findUnreadEntriesByUsersUuid(UUID userUuid, Pageable pageable);

    @Query("SELECT e FROM Entry e JOIN e.feed f JOIN f.users u WHERE u.id = :userId AND e.uuid = :entryUuid")
    Optional<Entry> findEntryByUuidAndUsersId(UUID entryUuid, Long userId);

    @Query("SELECT e FROM Entry e JOIN e.feed f JOIN f.users u WHERE u.uuid = :userUuid AND e.uuid = :entryUuid")
    Optional<Entry> findEntryByUuidAndUsersUuid(UUID entryUuid, UUID userUuid);

    @Query("SELECT e FROM Entry e JOIN e.feed f JOIN f.users u WHERE u.uuid = :userUuid AND e.uuid IN :entriesUuid")
    List<Entry> findEntriesByUuidAndUsersUuid(List<UUID> entriesUuid, UUID userUuid);

    @Query("SELECT e FROM Entry e JOIN e.feed f JOIN f.users u WHERE u.id = :userId AND e.link = :link")
    Optional<Entry> findEntryByLinkAndUsersId(String link, Long userId);

    @Query("SELECT e FROM Entry e JOIN e.feed f JOIN f.users u WHERE u.uuid = :userUuid AND e.link = :link")
    Optional<Entry> findEntryByLinkAndUsersUuid(String link, UUID userUuid);

    @Query("SELECT e FROM Entry e JOIN e.userEntryInteractions uei JOIN uei.user u WHERE u.id = :id AND uei.favorite = true")
    Page<Entry> findFavoriteEntriesByUsersId(Long id, Pageable pageable);

    @Query("SELECT e FROM Entry e JOIN e.userEntryInteractions uei JOIN uei.user u WHERE u.uuid = :uuid AND uei.favorite = true")
    Page<Entry> findFavoriteEntriesByUsersUuid(UUID uuid, Pageable pageable);

    @Query("SELECT e FROM Entry e JOIN e.userEntryInteractions uei JOIN uei.user u WHERE u.id = :id AND uei.bookmark = true")
    Page<Entry> findBookmarkedEntriesByUsersId(Long id, Pageable pageable);

    @Query("SELECT e FROM Entry e JOIN e.userEntryInteractions uei JOIN uei.user u WHERE u.uuid = :uuid AND uei.bookmark = true")
    Page<Entry> findBookmarkedEntriesByUsersUuid(UUID uuid, Pageable pageable);

    @Query("SELECT e FROM Entry e JOIN e.feed f JOIN f.users u WHERE u.id = :id AND (e.title LIKE %:searchString% OR e.text LIKE %:searchString%)")
    Page<Entry> searchEntriesByUsersId(Long id, String searchString, Pageable pageable);

    @Query("SELECT e FROM Entry e JOIN e.feed f JOIN f.users u WHERE u.uuid = :uuid AND (e.title LIKE %:searchString% OR e.text LIKE %:searchString%)")
    Page<Entry> searchEntriesByUsersUuid(UUID uuid, String searchString, Pageable pageable);

    @Query("SELECT e FROM Entry e JOIN e.feed f JOIN f.users u WHERE u.id = :userId AND f.uuid = :feedUuid AND (e.title LIKE %:searchString% OR e.text LIKE %:searchString%)")
    Page<Entry> searchEntriesByFeedUuidAndUsersId(UUID feedUuid, Long userId, String searchString, Pageable pageable);

    @Query("SELECT e FROM Entry e JOIN e.feed f JOIN f.users u WHERE u.uuid = :userUuid AND f.uuid = :feedUuid AND (e.title LIKE %:searchString% OR e.text LIKE %:searchString%)")
    Page<Entry> searchEntriesByFeedUuidAndUsersUuid(UUID feedUuid, UUID userUuid, String searchString, Pageable pageable);

    @Query("SELECT e FROM Entry e JOIN e.userEntryInteractions uei JOIN uei.user u WHERE u.id = :userId AND (e.title LIKE %:searchString% OR e.text LIKE %:searchString%)")
    Page<Entry> searchBookmarkedEntriesByUsersId(Long userId, String searchString, Pageable pageRequest);

    @Query("SELECT e FROM Entry e JOIN e.userEntryInteractions uei JOIN uei.user u WHERE u.uuid = :userUuid AND (e.title LIKE %:searchString% OR e.text LIKE %:searchString%)")
    Page<Entry> searchBookmarkedEntriesByUsersUuid(UUID userUuid, String searchString, Pageable pageRequest);

    @Query("SELECT e FROM Entry e JOIN e.userEntryInteractions uei JOIN uei.user u WHERE u.id = :userId AND (e.title LIKE %:searchString% OR e.text LIKE %:searchString%)")
    Page<Entry> searchFavoriteEntriesByUsersId(Long userId, String searchString, Pageable pageRequest);

    @Query("SELECT e FROM Entry e JOIN e.userEntryInteractions uei JOIN uei.user u WHERE u.uuid = :userUuid AND (e.title LIKE %:searchString% OR e.text LIKE %:searchString%)")
    Page<Entry> searchFavoriteEntriesByUsersUuid(UUID userUuid, String searchString, Pageable pageRequest);
}
