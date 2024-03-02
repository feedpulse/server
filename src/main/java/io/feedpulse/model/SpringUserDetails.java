package io.feedpulse.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class SpringUserDetails implements UserDetails {

    private static final long serialVersionUID = 1L;

//    private Long id;

    private UUID uuid;

    private String username;

    private String email;

    private boolean isUserEnabled;

    private boolean isUserLocked;

    //    @JsonIgnore
    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    public SpringUserDetails(UUID uuid, String email, String password,
                             boolean userEnabled, boolean userLocked, Collection<? extends GrantedAuthority> authorities) {
        this.uuid = uuid;
        this.username = email;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
        this.isUserEnabled = userEnabled;
        this.isUserLocked = userLocked;
    }

    public static SpringUserDetails build(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());

        return new SpringUserDetails(
                user.getUuid(),
                user.getEmail(),
                user.getPassword(),
                user.isUserEnabled(),
                user.isUserLocked(),
                authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isUserLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isUserEnabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        SpringUserDetails user = (SpringUserDetails) o;
        return Objects.equals(uuid, user.uuid);
    }
}
