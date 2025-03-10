package de.feedpulse.dto.request;

import de.feedpulse.model.Role;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public class UserUpdateRequestDTO implements Serializable {

    private Optional<String> email = Optional.empty();
    private Optional<String> password = Optional.empty();
    private Optional<String> newPassword = Optional.empty();
    private Optional<List<Role>> roles = Optional.empty();
    private Optional<Boolean> isUserEnabled = Optional.empty();

    public UserUpdateRequestDTO() {
    }

    public UserUpdateRequestDTO(String email, String password, String newPassword, List<Role> roles, Boolean isUserEnabled) {
        this.email = Optional.ofNullable(email);
        this.password = Optional.ofNullable(password);
        this.newPassword = Optional.ofNullable(newPassword);
        this.roles = Optional.ofNullable(roles);
        this.isUserEnabled = Optional.ofNullable(isUserEnabled);
    }

    public Optional<String> getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = Optional.ofNullable(email);
    }

    public Optional<String> getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = Optional.ofNullable(password);
    }

    public Optional<List<Role>> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = Optional.ofNullable(roles);
    }

    public Optional<String> getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = Optional.ofNullable(newPassword);
    }

    public Optional<Boolean> getIsUserEnabled() {
        return isUserEnabled;
    }

    public void setIsUserEnabled(Boolean isUserEnabled) {
        this.isUserEnabled = Optional.ofNullable(isUserEnabled);
    }

    @Override
    public String toString() {
        //TODO: remove password from toString method before production
        return "UserUpdateRequestDTO{" +
                "email=" + email +
                ", password=" + password +
                ", newPassword=" + newPassword +
                ", roles=" + roles +
                ", isUserEnabled=" + isUserEnabled +
                '}';
    }
}
