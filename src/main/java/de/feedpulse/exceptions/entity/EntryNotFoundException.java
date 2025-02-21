package de.feedpulse.exceptions.entity;

import de.feedpulse.exceptions.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class EntryNotFoundException extends BaseException {
    public EntryNotFoundException(String uuid) {
        super(
                HttpStatus.NOT_FOUND.value(),
                "Entry not found",
                "The entry with uuid: " + uuid + " was not found.",
                "Please provide a valid uuid and try again."
        );
    }
}
