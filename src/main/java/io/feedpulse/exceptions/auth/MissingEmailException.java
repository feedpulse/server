package io.feedpulse.exceptions.auth;

import io.feedpulse.exceptions.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class MissingEmailException extends BaseException {

    public MissingEmailException() {
        super(
                HttpStatus.BAD_REQUEST.value(),
                "Missing email",
                "You must provide an email.",
                "Check if the email is correct and try again."

        );
    }


}
