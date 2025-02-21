package de.feedpulse.exceptions.entity;

import de.feedpulse.exceptions.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class UserNotEnabledException extends BaseException {

    public UserNotEnabledException(String email) {
        super(
                HttpStatus.FORBIDDEN.value(),
                "User not enabled: " + email,
                "The user you are trying to access is not enabled.",
                "Contact the admin to enable the user."
        );
    }


}
