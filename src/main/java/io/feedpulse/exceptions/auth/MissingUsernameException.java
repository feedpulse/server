package io.feedpulse.exceptions.auth;

import io.feedpulse.exceptions.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class MissingUsernameException extends BaseException {

    public MissingUsernameException() {
        super(
                HttpStatus.BAD_REQUEST.value(),
                "Missing username",
                "You must provide a username.",
                "Check if the username is correct and try again."
        );
    }


}
