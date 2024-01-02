package dev.feder.exceptions;

import dev.feder.exceptions.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NoSuchEntryException extends BaseException {
    public NoSuchEntryException(String url) {
        super("No entry found with the uuid [" + url + "].");
    }
}
