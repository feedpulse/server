package de.feedpulse.exceptions.security;

import de.feedpulse.exceptions.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
public class TooManyRequestsException extends BaseException {

    public TooManyRequestsException() {
        super(
                HttpStatus.TOO_MANY_REQUESTS.value(),
                "Too many requests",
                "You have exceeded the maximum number of requests allowed.",
                "Please wait a few minutes and try again."
        );
    }
}
