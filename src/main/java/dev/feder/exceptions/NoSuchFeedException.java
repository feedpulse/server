package dev.feder.exceptions;

import dev.feder.exceptions.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND)
public class NoSuchFeedException extends BaseException {
    public NoSuchFeedException(String url) {
        super("No feed found with the uuid [" + url + "].");
    }
}
