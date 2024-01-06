package dev.feder.validation;

import org.springframework.lang.NonNull;

public class EmailValidator {

    private final static String EMAIL_REGEX = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";

    public static boolean isValid(@NonNull String value) {
        return value.matches(EMAIL_REGEX);
    }
}
