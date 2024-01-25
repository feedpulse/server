package io.feedpulse.exceptions;

public class MalformedFeedException extends BaseException {
    public MalformedFeedException(String url) {
        super("The feed at [" + url + "] could not be parsed.");
    }
}
