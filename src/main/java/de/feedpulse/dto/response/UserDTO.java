package de.feedpulse.dto.response;

import de.feedpulse.model.User;
import de.feedpulse.model.Role;

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
