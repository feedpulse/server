package dev.feder.repository;

import dev.feder.model.Feed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FeedRepository extends JpaRepository<Feed, UUID> {

    Optional<Feed> findByFeedUrl(String feedUrl);
    List<Feed> findFeedsByUsersId(Long userId);
    Page<Feed> findFeedsByUsersId( Long userId, Pageable pageable);
    Optional<Feed> findFeedByUuidAndUsersId(UUID uuid, Long userId);

}
