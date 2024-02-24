package io.feedpulse.exceptions.auth;

import io.feedpulse.exceptions.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class AuthenticationFailedException extends BaseException {

    public AuthenticationFailedException() {
        super(
                HttpStatus.UNAUTHORIZED.value(),
                "Authentication failed",
                "The credentials you provided are incorrect.",
                "Check if the credentials are correct and try again."
        );
    }


}
