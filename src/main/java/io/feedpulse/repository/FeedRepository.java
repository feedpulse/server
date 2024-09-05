package io.feedpulse.repository;

import io.feedpulse.model.Feed;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FeedRepository extends JpaRepository<Feed, UUID> {

    Optional<Feed> findByFeedUrl(String feedUrl);

    List<Feed> findFeedsByUsersId(Long userId);
    List<Feed> findFeedsByUsersUuid(UUID userUuid);

    Page<Feed> findFeedsByUsersId(Long userId, Pageable pageable);
    Page<Feed> findFeedsByUsersUuid(UUID userUuid, Pageable pageable);

    Optional<Feed> findFeedByUuidAndUsersUuid(UUID feedUuid, UUID userUuid);
    Optional<Feed> findFeedByUuidAndUsersId(UUID feedUuid, Long userId);

    @Query("SELECT f FROM Feed f JOIN f.users u WHERE u.id = :userId AND (f.title LIKE %:searchString% OR f.description LIKE %:searchString%)")
    Page<Feed> searchFeedsByUserId(Long userId, String searchString, Pageable pageRequest);
    @Query("SELECT f FROM Feed f JOIN f.users u WHERE u.uuid = :userUuid AND (f.title LIKE %:searchString% OR f.description LIKE %:searchString%)")
    Page<Feed> searchFeedsByUserUuid(UUID userUuid, String searchString, Pageable pageRequest);

    /**
     * Count the number of unread entries for a given feed and user
     * @param userUuid The UUID of the user
     * @param feedUuid The UUID of the feed
     * @return The number of unread entries
     */
    @NonNull
    @Query("SELECT COUNT(f) " +
            "FROM Feed f " +
            "JOIN f.entries e " +
            "JOIN f.users uf " +
            "JOIN User u ON uf.uuid = u.uuid " +
            "LEFT JOIN e.userEntryInteractions uei ON uei.user.uuid = :userUuid " + // LEFT JOIN to include entries without interaction
            "WHERE u.uuid = :userUuid " +
            //"AND uei.user.uuid = :userUuid " +
            "AND (uei.read IS NULL OR uei.read = false) " + // Handling null reads (no interaction yet)
            "AND f.uuid = :feedUuid")
    Integer countUnreadFeedEntries(@Param("userUuid") UUID userUuid, @Param("feedUuid") UUID feedUuid);
    /**
     * @Entity
     * public class UserUnreadEntry {
     *
     *     @Id
     *     @GeneratedValue
     *     private UUID uuid;
     *
     *     @ManyToOne
     *     @JoinColumn(name = "user_id")
     *     private User user;
     *
     *     @ManyToOne
     *     @JoinColumn(name = "entry_id")
     *     private Entry entry;
     * }
     */

    @NonNull
    @Query("SELECT (e.uuid) " +
            "FROM Feed f " +
            "JOIN f.entries e " +
            "JOIN f.users uf " +
            "JOIN User u ON uf.uuid = u.uuid " +
            "LEFT JOIN e.userEntryInteractions uei ON uei.user.uuid = :userUuid " + // LEFT JOIN to include entries without interaction
            "WHERE u.uuid = :userUuid " +
            "AND (uei.read IS NULL OR uei.read = false) " + // Handling null reads (no interaction yet)
            "AND f.uuid = :feedUuid")
    Page<UUID> getUuidOfUnreadFeedEntries(@Param("userUuid") UUID userUuid, @Param("feedUuid") UUID feedUuid, Pageable pageable);

}
