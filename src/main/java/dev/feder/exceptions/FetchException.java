package dev.feder.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class FetchException extends BaseException {

    public FetchException(String feedUrl) {
        super("Could not fetch content with url: " + feedUrl);
    }


}
