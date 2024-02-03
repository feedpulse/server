package io.feedpulse.exceptions;

import lombok.Getter;

import java.util.Date;

@Getter
public class BaseException extends RuntimeException {

    private final Date timestamp = new Date();
    private final int status;
    private String message;
    private String details;
    private String remedy;

    public BaseException(int status, String message) {
        super(message);
        this.status = status;
        this.message = message;
    }

    public BaseException(int status, String message, String details, String remedy) {
        super(message);
        this.status = status;
        this.message = message;
        this.details = details;
        this.remedy = remedy;
    }

    public String toJson() {
        return "{\"timestamp\":\"" + getTimestamp() + "\",\"status\":" + getStatus() + ",\"error\":\"" + getMessage() + "\",\"message\":\"" + getDetails() + "\",\"remedy\":\"" + getRemedy() + "\"}";
    }
}
