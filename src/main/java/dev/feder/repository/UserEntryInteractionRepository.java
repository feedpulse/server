package dev.feder.repository;

import dev.feder.model.Entry;
import dev.feder.model.UserEntryInteraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserEntryInteractionRepository extends JpaRepository<UserEntryInteraction, Long> {

    Optional<UserEntryInteraction> findByUserIdAndEntryUuid(Long userId, UUID entryId);

    @Query("SELECT uei FROM UserEntryInteraction uei JOIN uei.entry e WHERE uei.user.id = :userId AND e IN :entries")
    List<UserEntryInteraction> findByUserIdForEntries(Long userId, List<Entry> entries);
}
