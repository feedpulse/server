package io.feedpulse.repository;

import io.feedpulse.model.Feed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FeedRepository extends JpaRepository<Feed, UUID> {

    Optional<Feed> findByFeedUrl(String feedUrl);

    List<Feed> findFeedsByUsersId(Long userId);

    Page<Feed> findFeedsByUsersId(Long userId, Pageable pageable);

    Optional<Feed> findFeedByUuidAndUsersId(UUID uuid, Long userId);

    @Query("SELECT f FROM Feed f JOIN f.users u WHERE u.id = :userId AND (f.title LIKE %:searchString% OR f.description LIKE %:searchString%)")
    Page<Feed> searchFeedsByUserId(Long userId, String searchString, Pageable pageRequest);
}
