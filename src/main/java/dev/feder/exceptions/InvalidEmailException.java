package dev.feder.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidEmailException extends BaseException {

    public InvalidEmailException(String email) {
        super("Invalid email: " + email);
    }


}
