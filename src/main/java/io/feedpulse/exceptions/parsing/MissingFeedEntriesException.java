package io.feedpulse.exceptions.parsing;

import io.feedpulse.exceptions.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception thrown when no feed entries are found.
 */
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class MissingFeedEntriesException extends BaseException {

    public MissingFeedEntriesException(String feedUrl) {
        super(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "No feed entries found",
                "No feed entries found for feed with url: " + feedUrl,
                "Please check if the feed url is correct and try again."
        );
    }
}
