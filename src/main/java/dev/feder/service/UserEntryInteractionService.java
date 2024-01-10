package dev.feder.service;

import dev.feder.repository.UserEntryInteractionRepository;
import org.springframework.stereotype.Service;

@Service
public class UserEntryInteractionService {

    private final UserEntryInteractionRepository userEntryInteractionRepository;

    public UserEntryInteractionService(UserEntryInteractionRepository userEntryInteractionRepository) {
        this.userEntryInteractionRepository = userEntryInteractionRepository;
    }

}
