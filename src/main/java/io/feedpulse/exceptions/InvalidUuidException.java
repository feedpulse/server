package io.feedpulse.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.BAD_REQUEST)
public class InvalidUuidException extends BaseException {

    public InvalidUuidException(String url) {
        super(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid uuid: " + url,
                "Check if the uuid is correct and try again.",
                "Check if the uuid is correct and try again."
        );
    }


}
