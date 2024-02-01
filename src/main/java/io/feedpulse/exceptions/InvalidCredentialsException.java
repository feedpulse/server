package io.feedpulse.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidCredentialsException extends BaseException {

    public InvalidCredentialsException() {
        super(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid credentials",
                "Wrong email or password",
                "Check your credentials and try again"
        );
    }


}
