package io.feedpulse.validation;

import org.springframework.lang.NonNull;

import java.util.regex.Pattern;

public class EmailValidator {

    private final static String EMAIL_REGEX = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";

    public static boolean isValid(@NonNull String value) {
        return Pattern.matches(EMAIL_REGEX, value);
    }
}
