package io.feedpulse.validation;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.regex.Pattern;

public class PasswordValidator {

    private final static int MIN_LENGTH = 8;
    private final static int MAX_LENGTH = 128;
    private final static String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{" + MIN_LENGTH + "," + MAX_LENGTH + "}$";

    public static boolean isValid(@Nullable String value) {
        return true;
//        if (value == null) return false;
//        return Pattern.matches(PASSWORD_REGEX, value);
    }

}
