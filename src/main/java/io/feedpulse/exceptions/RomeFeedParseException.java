package io.feedpulse.exceptions;

import com.rometools.rome.io.FeedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown by WireFeedInput, WireFeedOutput, WireFeedParser and WireFeedGenerator instances if they can not parse or generate a feed.
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class RomeFeedParseException extends BaseException {
    public RomeFeedParseException(FeedException e) {
        super(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Cannot parse feed",
                "Can not parse or generate a feed",
                "Validate the feed url and try again. If the problem persists, please contact support."
        );
    }
}
