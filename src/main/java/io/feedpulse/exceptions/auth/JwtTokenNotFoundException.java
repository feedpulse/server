package io.feedpulse.exceptions.auth;

import io.feedpulse.exceptions.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class JwtTokenNotFoundException extends BaseException {

    public JwtTokenNotFoundException() {
        super(
                HttpStatus.UNAUTHORIZED.value(),
                "JWT token not found",
                "The JWT token is not present in the request.",
                "Please provide a valid JWT token and try again."
        );
    }

}
