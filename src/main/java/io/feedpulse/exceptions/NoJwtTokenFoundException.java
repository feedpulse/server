package io.feedpulse.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class NoJwtTokenFoundException extends BaseException {

    public NoJwtTokenFoundException() {
        super(
                HttpStatus.UNAUTHORIZED.value(),
                "No jwt token found",
                "Check if the jwt token is correct and try again.",
                "Check if the jwt token is correct and try again."
        );
    }

}
