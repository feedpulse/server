package io.feedpulse.exceptions.entity;

import io.feedpulse.exceptions.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND)
public class FeedNotFoundException extends BaseException {
    public FeedNotFoundException(String feedId) {
        super(
                HttpStatus.NOT_FOUND.value(),
                "Feed not found",
                "Feed with id: " + feedId + " not found.",
                "Please check if the feed id is correct and try again."
        );
    }
}
