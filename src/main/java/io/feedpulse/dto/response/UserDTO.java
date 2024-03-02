package io.feedpulse.dto.response;

import io.feedpulse.model.User;
import io.feedpulse.model.Role;

import java.util.Set;

public record UserDTO(

        String uuid,
        String username,
        String email,
        Set<Role> role,
        boolean enabled,
        boolean locked) {

    public static UserDTO of(User user) {
        return new UserDTO(
                user.getUuid().toString(),
                user.getUsername(),
                user.getEmail(),
                user.getRoles(),
                user.isUserEnabled(),
                user.isUserLocked()
        );
    }
}
