package io.feedpulse.controller;

import io.feedpulse.dto.request.UserUpdateRequestDTO;
import io.feedpulse.dto.response.PageableDTO;
import io.feedpulse.dto.response.SimpleUserDTO;
import io.feedpulse.exceptions.InvalidEmailException;
import io.feedpulse.exceptions.UserNotFoundInDbException;
import io.feedpulse.exceptions.WrongPasswordException;
import io.feedpulse.model.SpringUserDetails;
import io.feedpulse.model.User;
import io.feedpulse.service.EntryService;
import io.feedpulse.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public PageableDTO<SimpleUserDTO> getUsers(Pageable pageable) {
        return userService.getUsers(pageable);
    }


    @GetMapping("/filtered")
    @PreAuthorize("hasRole('ADMIN')")
    public PageableDTO<SimpleUserDTO> getUsersWithFilter(Pageable pageable,
                                                         @RequestParam(required = false) String email,
                                                         @RequestParam(required = false) Boolean isEnabled) {
        log.info("Getting users with filter: email={}, isEnabled={}", email, isEnabled);

        return userService.getUsersWithFilter(pageable, email, isEnabled);
    }

    @GetMapping("/me")
    public User getCurrentUser(@AuthenticationPrincipal SpringUserDetails userDetails) {
        return userService.getCurrentUserFromDb(userDetails);
    }

    @PostMapping("/me")
    public User updateUser(@RequestBody UserUpdateRequestDTO userUpdateRequestDTO, @AuthenticationPrincipal SpringUserDetails userDetails) throws UserNotFoundInDbException, InvalidEmailException, WrongPasswordException {
        return userService.updateUser(userUpdateRequestDTO, userDetails);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public User getUserById(@PathVariable Long id) throws UserNotFoundInDbException {
        return userService.getUserById(id);
    }

    /**
     * This is a workaround for the fact that Spring does not allow to have two endpoints with the same path but different parameters.
     * So if you want to get a user by email, you have to add the id '0' to the path.
     */
    @GetMapping("/0")
    @PreAuthorize("hasRole('ADMIN')")
    public User getUserById(@RequestParam String email) throws UserNotFoundInDbException {
        return userService.getUserByEmail(email);
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public User updateUser(@PathVariable Long id, @RequestBody UserUpdateRequestDTO userUpdateRequestDTO) throws UserNotFoundInDbException, InvalidEmailException, WrongPasswordException {
        return userService.updateUser(id, userUpdateRequestDTO);
    }

    @PostMapping("/{id}/enable")
    @PreAuthorize("hasRole('ADMIN')")
    public User enableUser(@PathVariable Long id, @RequestParam Boolean enable) throws UserNotFoundInDbException {
        return userService.enableUser(id, enable);
    }


}
