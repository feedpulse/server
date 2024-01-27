package io.feedpulse.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND)
public class NoSuchFeedException extends BaseException {
    public NoSuchFeedException(String url) {
        super(
                HttpStatus.NOT_FOUND.value(),
                "No such feed: " + url,
                "Check if the feed is correct and try again.",
                "Check if the feed is correct and try again."
        );
    }
}
