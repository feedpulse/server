package io.feedpulse.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.BAD_REQUEST)
public class InvalidUuidException extends BaseException {

    public InvalidUuidException(String url) {
        super("The uuid [" + url + "] is not valid.");
    }


}
