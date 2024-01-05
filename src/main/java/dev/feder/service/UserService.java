package dev.feder.service;

import dev.feder.model.Role;
import dev.feder.model.SpringUserDetails;
import dev.feder.model.User;
import dev.feder.repository.RoleRepository;
import dev.feder.repository.UserRepository;
import dev.feder.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;


@Service
public class UserService implements UserDetailsService {

    @Autowired
    AuthenticationManager authenticationManager;

    private JwtUtil jwtUtil;
    private UserRepository userRepository;
    private RoleRepository roleRepository;

    private PasswordEncoder passwordEncoder;


    public UserService(JwtUtil jwtUtil, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
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
        Role role = roleRepository.findByName(dev.feder.model.enums.Role.ROLE_USER).orElseThrow(
                () -> new RuntimeException("Role not found")
        );
        Set<Role> roles = Collections.singleton(role);
        User user = new User(username, password, email, roles);
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

        SpringUserDetails userDetails = (SpringUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return jwt;
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(String.format("User with username %s not found", username))
                );
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.emptyList()
        );
    }


}
