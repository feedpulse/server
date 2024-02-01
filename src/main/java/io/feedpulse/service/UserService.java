package io.feedpulse.service;

import io.feedpulse.dto.request.UserUpdateRequestDTO;
import io.feedpulse.exceptions.InvalidEmailException;
import io.feedpulse.exceptions.UserNotFoundInDbException;
import io.feedpulse.exceptions.WrongPasswordException;
import io.feedpulse.model.Role;
import io.feedpulse.model.SpringUserDetails;
import io.feedpulse.model.User;
import io.feedpulse.repository.RoleRepository;
import io.feedpulse.repository.UserRepository;
import io.feedpulse.util.JwtUtil;
import io.feedpulse.validation.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
public class UserService {

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

    public User updateUser(Long id, UserUpdateRequestDTO userUpdateRequestDTO) throws UserNotFoundInDbException, InvalidEmailException, WrongPasswordException {
        User updatedUser = getUserById(id);

        String email = userUpdateRequestDTO.getEmail().orElse(null);
        if (email != null) {
            if (!EmailValidator.isValid(email)) {
                throw new InvalidEmailException();
            }
            updatedUser.setEmail(email);
            updatedUser.setUsername(email);
        }

        String password = userUpdateRequestDTO.getPassword().orElse(null);
        String newPassword = userUpdateRequestDTO.getNewPassword().orElse(null);
        if (password != null && newPassword != null) {
            if (!passwordEncoder.matches(password, updatedUser.getPassword())) {
                throw new WrongPasswordException();
            }
            updatedUser.setPassword(passwordEncoder.encode(newPassword));
        }

        List<Role> roles = userUpdateRequestDTO.getRoles().orElse(null);
        if (roles != null && !roles.isEmpty()) {
            updatedUser.getRoles().clear();
            for (Role role : roles) {
                Optional<Role> dbRole = roleRepository.findByName(role.getName());
                if (dbRole.isPresent() && Objects.equals(role.getId(), dbRole.get().getId())) {
                    updatedUser.getRoles().add(dbRole.get());
                }
            }
        }
        updatedUser = userRepository.save(updatedUser);
        return updatedUser;
    }

    public User updateUser(UserUpdateRequestDTO userUpdateRequestDTO) throws UserNotFoundInDbException, InvalidEmailException, WrongPasswordException {
        User updatedUser = getCurrentUser();
        updatedUser = updateUser(updatedUser.getId(), userUpdateRequestDTO);
        return updatedUser;
    }

    @NonNull
    public User getCurrentUser() throws UserNotFoundInDbException {
        SpringUserDetails userDetails = (SpringUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return getUserById(userDetails.getId());
    }

    @NonNull
    public User getUserByToken(String token) throws UserNotFoundInDbException {
        String username = jwtUtil.extractUsername(token);
        return getUserByUsername(username);
    }

    @NonNull
    public User getUserById(Long id) throws UserNotFoundInDbException {
        return userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundInDbException("ID")
        );
    }

    @NonNull
    public User getUserByEmail(String email) throws UserNotFoundInDbException {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new UserNotFoundInDbException("Email"));
    }

    @NonNull
    public User getUserByUsername(String username) throws UserNotFoundInDbException {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundInDbException("Username")
        );
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

}
