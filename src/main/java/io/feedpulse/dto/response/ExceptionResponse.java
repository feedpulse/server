package io.feedpulse.dto.response;

import io.feedpulse.exceptions.BaseException;

import java.util.Date;

public record ExceptionResponse(Date timestamp, int status, String message, String details, String remedy) {

    static public ExceptionResponse fromException(BaseException exception) {
        return new ExceptionResponse(
                exception.getTimestamp(),
                exception.getStatus(),
                exception.getMessage(),
                exception.getDetails(),
                exception.getRemedy()
        );
    }
}


