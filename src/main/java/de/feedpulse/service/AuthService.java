package de.feedpulse.service;

import com.sanctionco.jmail.InvalidEmailException;
import de.feedpulse.exceptions.auth.*;
import de.feedpulse.exceptions.entity.*;
import de.feedpulse.model.enums.Role;
import de.feedpulse.dto.request.AccountRequestDTO;
import de.feedpulse.model.ReferralCode;
import de.feedpulse.model.User;
import de.feedpulse.repository.RoleRepository;
import de.feedpulse.repository.UserRepository;
import de.feedpulse.util.JwtUtil;
import de.feedpulse.validation.EmailValidator;
import de.feedpulse.validation.PasswordValidator;
import de.feedpulse.validation.UuidValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    private MailService mailService;

    public AuthService(JwtUtil jwtUtil, UserRepository userRepository, RoleRepository roleRepository, ReferralCodeService referralCodeService, PasswordEncoder passwordEncoder, MailService mailService) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.referralCodeService = referralCodeService;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
    }

    public User createUser(Map<String, String> params) {
        String email = params.getOrDefault("email", null);
        String password = params.getOrDefault("password", null);
        validateNewUser(email, email, password);

        return createUser(email, email, password);
    }

    private void validateNewUser(@Nullable String email, @Nullable String username, @Nullable String password){
        validateExistingUser(email, username, password);

        // database checks at last to avoid unnecessary checks with the database (in case the previous checks fail)
        if (userRepository.findByEmail(email).isPresent()) {
            throw new EmailConflictException(email);
        }
    }

    private void validateExistingUser(@Nullable String email, @Nullable String username, @Nullable String password) {
        if (email == null) throw new MissingEmailException();
        if (!EmailValidator.isValid(email)) throw new InvalidEmailException();

        if (username == null) throw new MissingUsernameException();

        if (password == null) throw new MissingPasswordException();
        if (!PasswordValidator.isValid(password)) throw new InvalidPasswordException();
    }


    private User createUser(@NonNull String email, @NonNull String username, @NonNull String password) {
        password = passwordEncoder.encode(password);
        de.feedpulse.model.Role role = roleRepository.findByName(Role.ROLE_USER).orElseThrow(RoleNotFoundException::new);
        Set<de.feedpulse.model.Role> roles = new HashSet<>();
        roles.add(role);
        User user = new User(username, password, email, roles);
        return userRepository.save(user);
    }

    public String loginUser(Map<String, String> params) {
        String email = params.getOrDefault("email", null);
        String password = params.getOrDefault("password", null);
        validateExistingUser(email, email, password);

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
            updateTimestamps(userRepository.findByEmail(email).get());
        } catch (DisabledException e) {
            throw new UserNotEnabledException(email);
        } catch (LockedException e) {
            throw new UserLockedException(email);
        } catch (AuthenticationException e) {
            throw new AuthenticationFailedException();
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtUtil.generateToken(authentication);
    }

    private User updateTimestamps(@NonNull User user) {
        user.setDateLastLogin(LocalDate.now());
        return userRepository.save(user);
    }


    public void requestAccount(AccountRequestDTO accountRequestDTO) {
        String email = accountRequestDTO.getEmail().orElse(null);
        String password = accountRequestDTO.getPassword().orElse(null);
        validateNewUser(email, email, password);

        String referralCode = accountRequestDTO.getReferralCode().orElseThrow(MissingReferralCodeException::new);
        if (!UuidValidator.isValid(referralCode)) throw new InvalidReferralCodeException(referralCode);
        ReferralCode rc = referralCodeService.findByCode(referralCode).orElseThrow(() -> new ReferralCodeNotFoundException(referralCode));

        User user = createUser(email, email, password);
        user.setUserEnabled(false);
        user = userRepository.save(user);

        referralCodeService.invalidateReferralCode(rc, user);

        mailService.sendAccountRequestMail(email);
        mailService.sendAccountRequestMailToAdmin(user);
    }
}
