package de.feedpulse.service;

import de.feedpulse.model.Feed;
import de.feedpulse.repository.EntryRepository;
import de.feedpulse.repository.UserEntryInteractionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UserEntryInteractionService {

    private final UserEntryInteractionRepository userEntryInteractionRepository;
    private final EntryRepository entryRepository;

    public UserEntryInteractionService(UserEntryInteractionRepository userEntryInteractionRepository, EntryRepository entryRepository) {
        this.userEntryInteractionRepository = userEntryInteractionRepository;
        this.entryRepository = entryRepository;
    }

    @Transactional
    public void deleteAllUserEntryInteractionsForFeed(Long userId, Feed feed) {
        List<UUID> entryUuids = entryRepository.findUuidsByFeedId(feed);
        if(!entryUuids.isEmpty()){
            userEntryInteractionRepository.deleteByUserAndEntries(userId, entryUuids);
        }
    }
}
