package de.feedpulse.exceptions.common;

import de.feedpulse.exceptions.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidRequestBodyException extends BaseException {
    public InvalidRequestBodyException() {
        super(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid request body",
                "Check if the request body is correct and try again.",
                "Check if the request body is correct and try again."
        );
    }
}
