package io.feedpulse.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class FetchException extends BaseException {

    public FetchException(String feedUrl) {
        super(
                HttpStatus.BAD_REQUEST.value(),
                "Could not fetch feed from url: " + feedUrl,
                "Check if the url is correct and try again.",
                "Check if the url is correct and try again."
        );
    }


}
