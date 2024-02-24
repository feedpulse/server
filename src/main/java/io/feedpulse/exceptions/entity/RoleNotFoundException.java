package io.feedpulse.exceptions.entity;

import io.feedpulse.exceptions.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RoleNotFoundException extends BaseException {
    public RoleNotFoundException() {
        super(
                HttpStatus.NOT_FOUND.value(),
                "Role not found in db",
                "Check if the role exists in the db and try again.",
                "Check if the role exists in the db and try again."
        );
    }
}
