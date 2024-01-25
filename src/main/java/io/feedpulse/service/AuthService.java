package io.feedpulse.service;

import io.feedpulse.model.Role;
import io.feedpulse.model.User;
import io.feedpulse.repository.RoleRepository;
import io.feedpulse.repository.UserRepository;
import io.feedpulse.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

@Service
public class AuthService {

    @Autowired
    AuthenticationManager authenticationManager;

    private JwtUtil jwtUtil;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    public AuthService(JwtUtil jwtUtil, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(Map<String, String> params) {
        String email = params.getOrDefault("email", null);
        if (email == null) throw new IllegalArgumentException("Email is required");

        String username = email; //params.getOrDefault("username", null);
        if (username == null) throw new IllegalArgumentException("Username is required");

        String password = params.getOrDefault("password", null);
        if (password == null) throw new IllegalArgumentException("Password is required");

        return createUser(email, username, password);
    }


    public User createUser(@NonNull String email, @NonNull String username, @NonNull String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        password = passwordEncoder.encode(password);
        Role role = roleRepository.findByName(io.feedpulse.model.enums.Role.ROLE_USER).orElseThrow(
                () -> new RuntimeException("Role not found")
        );
        Set<Role> roles = Collections.singleton(role);
        User user = new User(username, password, email, roles, Collections.emptyList(), Collections.emptyList());
        return userRepository.save(user);
    }

    public String loginUser(Map<String, String> params) {
        String email = params.getOrDefault("email", null);
        if (email == null) throw new IllegalArgumentException("Email is required");

        String password = params.getOrDefault("password", null);
        if (password == null) throw new IllegalArgumentException("Password is required");

        return loginUser(email, password);
    }

    public String loginUser(@NonNull String email, @NonNull String password) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtil.generateToken(authentication);
        return jwt;
    }

}
