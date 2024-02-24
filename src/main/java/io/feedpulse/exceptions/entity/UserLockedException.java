package io.feedpulse.exceptions.entity;

import io.feedpulse.exceptions.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class UserLockedException extends BaseException {

    public UserLockedException(String email) {
        super(
                HttpStatus.FORBIDDEN.value(),
                "User locked: " + email,
                "The user you are trying to access is locked.",
                "Contact the admin to unlock the user."
        );
    }


}
