package io.feedpulse.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class WrongPasswordException extends BaseException {

    public WrongPasswordException() {
        super(
                HttpStatus.BAD_REQUEST.value(),
                "Wrong password",
                "Check if the password is correct and try again.",
                "Check if the password is correct and try again."
        );
    }


}
