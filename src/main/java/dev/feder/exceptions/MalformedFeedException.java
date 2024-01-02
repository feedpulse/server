package dev.feder.exceptions;

import dev.feder.exceptions.BaseException;

public class MalformedFeedException extends BaseException {
    public MalformedFeedException(String url) {
        super("The feed at [" + url + "] could not be parsed.");
    }
}
