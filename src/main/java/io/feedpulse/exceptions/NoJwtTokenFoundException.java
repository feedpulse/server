package io.feedpulse.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class NoJwtTokenFoundException extends BaseException {

    public NoJwtTokenFoundException() {
        super("No JWT token found in request headers.");
    }

}
