package io.feedpulse.exceptions;

import org.springframework.http.HttpStatus;

public class HtmlNotParsableException extends BaseException {
    public HtmlNotParsableException(String url) {
        super(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Could not parse html from url: " + url,
                "Check if the url is correct and try again.",
                "Check if the url is correct and try again."
        );
    }
}
