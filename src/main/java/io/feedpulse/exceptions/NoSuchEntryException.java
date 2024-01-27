package io.feedpulse.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NoSuchEntryException extends BaseException {
    public NoSuchEntryException(String url) {
        super(
                HttpStatus.NOT_FOUND.value(),
                "No such entry: " + url,
                "Check if the entry is correct and try again.",
                "Check if the entry is correct and try again."
        );
    }
}
