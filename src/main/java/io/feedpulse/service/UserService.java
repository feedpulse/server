package io.feedpulse.service;

import com.sanctionco.jmail.InvalidEmailException;
import io.feedpulse.dto.request.UserUpdateRequestDTO;
import io.feedpulse.dto.response.PageableDTO;
import io.feedpulse.dto.response.SimpleUserDTO;
import io.feedpulse.exceptions.auth.InvalidPasswordException;
import io.feedpulse.exceptions.entity.UserNotFoundException;
import io.feedpulse.model.Role;
import io.feedpulse.model.SpringUserDetails;
import io.feedpulse.model.User;
import io.feedpulse.repository.RoleRepository;
import io.feedpulse.repository.UserRepository;
import io.feedpulse.specification.SearchCriteria;
import io.feedpulse.specification.UserSpecification;
import io.feedpulse.util.JwtUtil;
import io.feedpulse.validation.EmailValidator;
import io.feedpulse.validation.PasswordValidator;
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

import java.util.List;
import java.util.Objects;
import java.util.Optional;


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

    public User updateUser(Long id, UserUpdateRequestDTO userUpdateRequestDTO) {
        User updatedUser = getUserById(id);

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
        Boolean isUserEnabled = userUpdateRequestDTO.getIsUserEnabled().orElse(null);
        if (isUserEnabled != null) {
            updatedUser.setUserEnabled(isUserEnabled);
        }
        updatedUser = userRepository.save(updatedUser);
        return updatedUser;
    }

    public User updateUser(UserUpdateRequestDTO userUpdateRequestDTO, SpringUserDetails userDetails) {
        return updateUser(userDetails.getId(), userUpdateRequestDTO);
    }

    public User enableUser(Long id, Boolean enable) throws UserNotFoundException {
        if (enable == null) {
            throw new IllegalArgumentException("Enable parameter cannot be null");
        }
        User user = getUserById(id);
        boolean wasEnabled = user.isUserEnabled();
        user.setUserEnabled(enable);
        user = userRepository.save(user);
        if (!wasEnabled && enable) {
            mailService.sendAccountRequestSuccessfulMail();
        }
        return user;
    }

    public User getCurrentUserFromDb(SpringUserDetails userDetails) {
        return getUserById(userDetails.getId());
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

    public List<User> getAllAdmins() {
        return userRepository.findAllByRole(io.feedpulse.model.enums.Role.ROLE_ADMIN);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public PageableDTO<SimpleUserDTO> getUsers(Pageable pageable) {
        Page<User> userList = userRepository.findAll(pageable);
        PagedModel<EntityModel<User>> pagedModel = pagedResourcesAssembler.toModel(userList);
        List<SimpleUserDTO> simpleUserDTOs = convertToSimpleUserDTO(pagedModel);
        return PageableDTO.of(pagedModel, simpleUserDTOs);
    }

    public PageableDTO<SimpleUserDTO> getUsersWithFilter(Pageable pageable, String email, Boolean isEnabled) {
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
        List<SimpleUserDTO> simpleUserDTOs = convertToSimpleUserDTO(pagedModel);
        return PageableDTO.of(pagedModel, simpleUserDTOs);
    }

    private static List<SimpleUserDTO> convertToSimpleUserDTO(PagedModel<EntityModel<User>> pagedModel) {
        return pagedModel.getContent().stream()
                .map(EntityModel::getContent)
                .filter(Objects::nonNull)
                .map(SimpleUserDTO::of)
                .toList();
    }
}
