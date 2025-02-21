package de.feedpulse.service;

import com.sanctionco.jmail.InvalidEmailException;
import de.feedpulse.model.enums.Role;
import de.feedpulse.dto.request.UserUpdateRequestDTO;
import de.feedpulse.dto.response.PageableDTO;
import de.feedpulse.dto.response.UserDTO;
import de.feedpulse.exceptions.auth.InvalidPasswordException;
import de.feedpulse.exceptions.entity.UserNotFoundException;
import de.feedpulse.model.SpringUserDetails;
import de.feedpulse.model.User;
import de.feedpulse.repository.RoleRepository;
import de.feedpulse.repository.UserRepository;
import de.feedpulse.specification.SearchCriteria;
import de.feedpulse.specification.UserSpecification;
import de.feedpulse.util.JwtUtil;
import de.feedpulse.validation.EmailValidator;
import de.feedpulse.validation.PasswordValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;


@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    AuthenticationManager authenticationManager;

    private JwtUtil jwtUtil;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private final PagedResourcesAssembler<User> pagedResourcesAssembler;
    private final MailService mailService;


    public UserService(JwtUtil jwtUtil, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, PagedResourcesAssembler<User> pagedResourcesAssembler, @Lazy MailService mailService) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
        this.mailService = mailService;
    }

    public User updateUser(UUID id, UserUpdateRequestDTO userUpdateRequestDTO) {
        User updatedUser = this.getUserByUuid(id);

        String email = userUpdateRequestDTO.getEmail().orElse(null);
        if (email != null) {
            if (!EmailValidator.isValid(email)) throw new InvalidEmailException();
            updatedUser.setEmail(email);
            updatedUser.setUsername(email);
        }

        String password = userUpdateRequestDTO.getPassword().orElse(null);
        String newPassword = userUpdateRequestDTO.getNewPassword().orElse(null);
        if (password != null && newPassword != null) {
            if (!PasswordValidator.isValid(newPassword)) throw new InvalidPasswordException();
            if (!passwordEncoder.matches(password, updatedUser.getPassword())) throw new InvalidPasswordException();
            updatedUser.setPassword(passwordEncoder.encode(newPassword));
        }

        List<de.feedpulse.model.Role> roles = userUpdateRequestDTO.getRoles().orElse(null);
        if (roles != null && !roles.isEmpty()) {
            updatedUser.getRoles().clear();
            for (de.feedpulse.model.Role role : roles) {
                Optional<de.feedpulse.model.Role> dbRole = roleRepository.findByName(role.getName());
                if (dbRole.isPresent() && Objects.equals(role.getId(), dbRole.get().getId())) {
                    updatedUser.getRoles().add(dbRole.get());
                }
            }
        }
        Boolean isUserEnabled = userUpdateRequestDTO.getIsUserEnabled().orElse(null);
        if (isUserEnabled != null) {
            updatedUser.setUserEnabled(isUserEnabled);
        }
        updatedUser.setDateUpdated(LocalDate.now());
        updatedUser = userRepository.save(updatedUser);
        return updatedUser;
    }

    public User updateUser(UserUpdateRequestDTO userUpdateRequestDTO, SpringUserDetails userDetails) {
        return updateUser(userDetails.getUuid(), userUpdateRequestDTO);
    }

    public User enableUser(UUID uuid, Boolean enable) throws UserNotFoundException {
        if (enable == null) {
            throw new IllegalArgumentException("Enable parameter cannot be null");
        }
        User user = getUserByUuid(uuid);
        boolean wasEnabled = user.isUserEnabled();
        user.setUserEnabled(enable);
        user = userRepository.save(user);
        if (!wasEnabled && enable) {
            mailService.sendAccountRequestSuccessfulMail();
        }
        return user;
    }

    public User getCurrentUserFromDb(SpringUserDetails userDetails) {
        return getUserByUuid(userDetails.getUuid());
    }

    @NonNull
    public User getUserByToken(String token) throws UserNotFoundException {
        String username = jwtUtil.extractUsername(token);
        return getUserByUsername(username);
    }

    @NonNull
    public User getUserById(Long id) throws UserNotFoundException {
        return userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("ID", id.toString())
        );
    }

    @NonNull
    public User getUserByEmail(String email) throws UserNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new UserNotFoundException("Email", email));
    }

    @NonNull
    public User getUserByUsername(String username) throws UserNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundException("Username", username)
        );
    }


    public User getUserByUuid(UUID userUuid) {
        return userRepository.findByUuid(userUuid).orElseThrow(
                () -> new UserNotFoundException("UUID", userUuid.toString())
        );
    }


    public List<User> getAllAdmins() {
        return userRepository.findAllByRole(Role.ROLE_ADMIN);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public PageableDTO<UserDTO> getUsers(Pageable pageable) {
        Page<User> userList = userRepository.findAll(pageable);
        PagedModel<EntityModel<User>> pagedModel = pagedResourcesAssembler.toModel(userList);
        List<UserDTO> userDTOS = convertToSimpleUserDTO(pagedModel);
        return PageableDTO.of(pagedModel, userDTOS);
    }

    public PageableDTO<UserDTO> getUsersWithFilter(Pageable pageable, String email, Boolean isEnabled) {
        Specification<User> spec = Specification.where(null);

        if (email != null && !email.isEmpty()) {
            Specification<User> emailSpec = new UserSpecification(new SearchCriteria("email", ":", email));
            spec = spec.and(emailSpec);
        }

        if (isEnabled != null) {
            Specification<User> isEnabledSpec = new UserSpecification(new SearchCriteria("isUserEnabled", ":", isEnabled));
            spec = spec.and(isEnabledSpec);
        }

        Page<User> userList = userRepository.findAll(spec, pageable);
        PagedModel<EntityModel<User>> pagedModel = pagedResourcesAssembler.toModel(userList);
        List<UserDTO> userDTOS = convertToSimpleUserDTO(pagedModel);
        return PageableDTO.of(pagedModel, userDTOS);
    }

    private static List<UserDTO> convertToSimpleUserDTO(PagedModel<EntityModel<User>> pagedModel) {
        return pagedModel.getContent().stream()
                .map(EntityModel::getContent)
                .filter(Objects::nonNull)
                .map(UserDTO::of)
                .toList();
    }
}
