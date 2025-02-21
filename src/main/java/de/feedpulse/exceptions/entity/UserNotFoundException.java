package de.feedpulse.exceptions.entity;

import de.feedpulse.exceptions.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends BaseException {
    public UserNotFoundException(String findBy, String value) {
        super(
                HttpStatus.NOT_FOUND.value(),
                "User not found in db by [" + findBy + " = " + value + "]",
                "Check if the user exists in the db and try again.",
                "Check if the user exists in the db and try again."
        );
    }
}
