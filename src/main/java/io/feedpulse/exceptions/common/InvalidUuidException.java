package io.feedpulse.exceptions.common;

import io.feedpulse.exceptions.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.BAD_REQUEST)
public class InvalidUuidException extends BaseException {

    public InvalidUuidException(@Nullable String url) {
        super(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid uuid: '" + url + "'",
                "The uuid is not valid.",
                "Please provide a valid uuid and try again."
        );
    }


}
