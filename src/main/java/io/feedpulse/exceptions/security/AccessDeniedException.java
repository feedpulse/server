package io.feedpulse.exceptions.security;

import io.feedpulse.exceptions.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class AccessDeniedException extends BaseException {

    public static final int STATUS = HttpStatus.FORBIDDEN.value();

    public AccessDeniedException() {
        super(
                HttpStatus.FORBIDDEN.value(),
                "Access denied",
                "You are not authorized to access this resource.",
                "You are not authorized to access this resource."
        );
    }
}
