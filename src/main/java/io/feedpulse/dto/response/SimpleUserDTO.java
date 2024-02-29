package io.feedpulse.dto.response;

import io.feedpulse.model.User;
import io.feedpulse.model.Role;

import java.util.Base64;
import java.util.Set;

public record SimpleUserDTO(

        String id,
        String username,
        String email,
        Set<Role> role,
        boolean enabled,
        boolean locked) {

    public static SimpleUserDTO of(User user) {
        String id = Base64.getEncoder().encodeToString(user.getId().toString().getBytes());
        return new SimpleUserDTO(
                id,
                user.getUsername(),
                user.getEmail(),
                user.getRoles(),
                user.isUserEnabled(),
                user.isUserLocked()
        );
    }
}
