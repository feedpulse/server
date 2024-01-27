package io.feedpulse.exceptions;

import org.springframework.http.HttpStatus;

public class MalformedFeedException extends BaseException {
    public MalformedFeedException(String url) {
        super(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Could not parse feed from url: " + url,
                "Check if the url is correct and try again.",
                "Check if the url is correct and try again."
        );
    }
}
