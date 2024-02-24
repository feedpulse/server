package io.feedpulse.exceptions.auth;

import io.feedpulse.exceptions.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class MissingPasswordException extends BaseException {

    public MissingPasswordException() {
        super(
                HttpStatus.BAD_REQUEST.value(),
                "Missing password",
                "You must provide a password.",
                "Check if the password is correct and try again."
        );
    }


}
