package io.feedpulse.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class NotAuthenticatedException extends BaseException {

    public NotAuthenticatedException() {
        super("You are not authenticated. Please login or register.");
    }

}
