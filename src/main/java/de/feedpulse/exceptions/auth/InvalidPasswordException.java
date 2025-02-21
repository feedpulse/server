package de.feedpulse.exceptions.auth;

import de.feedpulse.exceptions.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidPasswordException extends BaseException {

    public InvalidPasswordException() {
        super(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid password",
                "The password you entered is invalid.",
                "Check if the password is correct and try again."
        );
    }


}
