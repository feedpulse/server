package io.feedpulse.dto.response;

import io.feedpulse.model.User;
import io.feedpulse.model.Role;

import java.util.Set;

public record SimpleUserDTO(
        String username,
        String email,
        Set<Role> role,
        boolean enabled,
        boolean locked) {

    public static SimpleUserDTO of(User user) {
        return new SimpleUserDTO(
                user.getUsername(),
                user.getEmail(),
                user.getRoles(),
                user.isUserEnabled(),
                user.isUserLocked()
        );
    }
}
