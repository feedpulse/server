package de.feedpulse.validation;

import org.springframework.lang.Nullable;

import java.util.UUID;

public class UuidValidator {

    public static boolean isValid(@Nullable String value) {
        if (value == null) return false;
        try {
            UUID.fromString(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

}
