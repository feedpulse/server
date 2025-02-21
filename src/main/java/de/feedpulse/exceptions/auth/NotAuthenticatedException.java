package de.feedpulse.exceptions.auth;

import de.feedpulse.exceptions.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class NotAuthenticatedException extends BaseException {

    public NotAuthenticatedException() {
        super(
                HttpStatus.UNAUTHORIZED.value(),
                "Not authenticated",
                "The user is not authenticated.",
                "Please login and try again."
        );
    }

}
