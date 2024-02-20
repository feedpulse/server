package io.feedpulse.repository;

import io.feedpulse.model.Entry;
import io.feedpulse.model.UserEntryInteraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserEntryInteractionRepository extends JpaRepository<UserEntryInteraction, Long> {

    Optional<UserEntryInteraction> findByUserIdAndEntryUuid(Long userId, UUID entryId);

    @Query("SELECT uei FROM UserEntryInteraction uei JOIN uei.entry e WHERE uei.user.id = :userId AND e IN :entries")
    List<UserEntryInteraction> findByUserIdForEntries(Long userId, List<Entry> entries);

    @Modifying
    @Query(value = "DELETE FROM UserEntryInteraction u WHERE u.user.id = :userId AND u.entry.uuid IN :entryUuids")
    void deleteByUserAndEntries(Long userId, List<UUID> entryUuids);

}
