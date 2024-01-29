package io.feedpulse.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception thrown when no feed entries are found.
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class NoFeedEntriesFoundException extends BaseException {

    public NoFeedEntriesFoundException(String feedUrl) {
        super(
                HttpStatus.BAD_REQUEST.value(),
                "No feed entries found",
                "No entries found for feed with url: " + feedUrl,
                "Validate that the feed url is correct and that the feed contains entries."

        );
    }
}
