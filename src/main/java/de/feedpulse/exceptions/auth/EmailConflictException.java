package de.feedpulse.exceptions.auth;

import de.feedpulse.exceptions.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class EmailConflictException extends BaseException {

    public EmailConflictException(String email) {
        super(
                HttpStatus.BAD_REQUEST.value(),
                "Email already exists: " + email,
                "The email you entered already exists in the db.",
                "Check if the email is correct and try again."
        );
    }


}
