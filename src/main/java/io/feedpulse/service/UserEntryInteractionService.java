package io.feedpulse.service;

import io.feedpulse.repository.UserEntryInteractionRepository;
import org.springframework.stereotype.Service;

@Service
public class UserEntryInteractionService {

    private final UserEntryInteractionRepository userEntryInteractionRepository;

    public UserEntryInteractionService(UserEntryInteractionRepository userEntryInteractionRepository) {
        this.userEntryInteractionRepository = userEntryInteractionRepository;
    }

}
