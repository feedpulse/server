package io.feedpulse.validation;

import org.springframework.lang.Nullable;

import java.util.regex.Pattern;

public class EmailValidator {

    private final static String EMAIL_REGEX = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";

    public static boolean isValid(@Nullable String value) {
        if (value == null) return false;
        return Pattern.matches(EMAIL_REGEX, value);
    }
}
