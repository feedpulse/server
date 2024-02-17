package io.feedpulse.service;

import io.feedpulse.dto.request.AccountRequestDTO;
import io.feedpulse.exceptions.InvalidCredentialsException;
import io.feedpulse.exceptions.InvalidEmailException;
import io.feedpulse.exceptions.WrongPasswordException;
import io.feedpulse.model.ReferralCode;
import io.feedpulse.model.Role;
import io.feedpulse.model.User;
import io.feedpulse.repository.RoleRepository;
import io.feedpulse.repository.UserRepository;
import io.feedpulse.util.JwtUtil;
import io.feedpulse.validation.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    AuthenticationManager authenticationManager;

    private JwtUtil jwtUtil;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private ReferralCodeService referralCodeService;
    private PasswordEncoder passwordEncoder;

    public AuthService(JwtUtil jwtUtil, UserRepository userRepository, RoleRepository roleRepository, ReferralCodeService referralCodeService, PasswordEncoder passwordEncoder) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.referralCodeService = referralCodeService;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(Map<String, String> params) {
        String email = params.getOrDefault("email", null);
        if (email == null) throw new InvalidEmailException();

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
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        User user = new User(username, password, email, roles);
        return userRepository.save(user);
    }

    public String loginUser(Map<String, String> params) {
        String email = params.getOrDefault("email", null);
        if (email == null || email.isBlank() || !EmailValidator.isValid(email)) throw new InvalidEmailException();

        String password = params.getOrDefault("password", null);
        if (password == null || password.isBlank()) throw new WrongPasswordException();

        return loginUser(email, password);
    }

    public String loginUser(@NonNull String email, @NonNull String password) {
        UsernamePasswordAuthenticationToken authenticationToken;
        Authentication authentication;

        try {
            authenticationToken = new UsernamePasswordAuthenticationToken(email, password);
            /**
             * This will call the loadUserByUsername method in SpringUserDetailsService to get the user details
             */
            authentication = authenticationManager.authenticate(authenticationToken);
        } catch (DisabledException e) {
            throw new DisabledException("User is disabled");
        } catch (LockedException e) {
            throw new LockedException("User is locked");
        } catch (AuthenticationException e) {
            e.printStackTrace();
            throw new InvalidCredentialsException();
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtUtil.generateToken(authentication);
    }


    public void requestAccount(AccountRequestDTO accountRequestDTO) {
        String email = accountRequestDTO.getEmail().orElseThrow(() -> new IllegalArgumentException("Email is required"));
        String password = accountRequestDTO.getPassword().orElseThrow(() -> new IllegalArgumentException("Password is required"));
        String referralCode = accountRequestDTO.getReferralCode().orElseThrow(() -> new IllegalArgumentException("Referral code is required"));
        // handle referral code
        ReferralCode rc = referralCodeService.findByCode(referralCode).orElseThrow(() -> new IllegalArgumentException("Invalid referral code"));

        User user = createUser(email, email, password);
        user.setUserEnabled(false);
        user = userRepository.save(user);
        referralCodeService.invalidateReferralCode(rc, user);


    }
}
