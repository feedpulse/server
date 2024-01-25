package io.feedpulse.exceptions;

public class HtmlNotParsableException extends BaseException {
    public HtmlNotParsableException(String url) {
        super("The html of the url " + url + " is not parsable.");
    }
}
