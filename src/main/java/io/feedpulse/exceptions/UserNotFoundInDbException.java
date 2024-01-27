package io.feedpulse.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundInDbException extends BaseException{
    public UserNotFoundInDbException(String findBy) {
        super(
                HttpStatus.NOT_FOUND.value(),
                "User not found in db by [" + findBy + "]",
                "Check if the user exists in the db and try again.",
                "Check if the user exists in the db and try again."
        );
    }
}
