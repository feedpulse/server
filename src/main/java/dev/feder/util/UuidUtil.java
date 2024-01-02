package dev.feder.util;

import dev.feder.exceptions.InvalidUuidException;
import org.springframework.lang.Nullable;

import java.util.UUID;

public class UuidUtil {

    public UuidUtil(){
    }

    public static UUID fromString(@Nullable String uuidString) throws InvalidUuidException {
        UUID uuid;
        try {
            if (uuidString == null) {
                throw new InvalidUuidException("UUID string is null");
            }
            uuid = UUID.fromString(uuidString);
        } catch (IllegalArgumentException e) {
            throw new InvalidUuidException(uuidString != null ? uuidString : "null");
        }
        return uuid;
    }
}
