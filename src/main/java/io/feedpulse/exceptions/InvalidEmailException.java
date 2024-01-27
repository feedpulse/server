package io.feedpulse.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidEmailException extends BaseException {

    public InvalidEmailException(String email) {
        super(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid email: " + email,
                "Check if the email is correct and try again.",
                "Check if the email is correct and try again."
        );
    }


}
