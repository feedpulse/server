package io.feedpulse.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class WrongPasswordException extends BaseException {

    public WrongPasswordException() {
        super("the entered password is wrong");
    }


}
