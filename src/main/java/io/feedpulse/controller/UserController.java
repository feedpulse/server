package io.feedpulse.controller;

import io.feedpulse.dto.request.UserUpdateRequestDTO;
import io.feedpulse.dto.response.PageableDTO;
import io.feedpulse.dto.response.UserDTO;
import io.feedpulse.exceptions.entity.UserNotFoundException;
import io.feedpulse.model.SpringUserDetails;
import io.feedpulse.model.User;
import io.feedpulse.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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
    public PageableDTO<UserDTO> getUsers(Pageable pageable) {
        return userService.getUsers(pageable);
    }


    @GetMapping("/filtered")
    @PreAuthorize("hasRole('ADMIN')")
    public PageableDTO<UserDTO> getUsersWithFilter(Pageable pageable,
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
    public User updateUser(@RequestBody UserUpdateRequestDTO userUpdateRequestDTO, @AuthenticationPrincipal SpringUserDetails userDetails) {
        return userService.updateUser(userUpdateRequestDTO, userDetails);
    }

    @GetMapping("/{uuid}")
    @PreAuthorize("hasRole('ADMIN')")
    public User getUserByUuid(@PathVariable UUID uuid) throws UserNotFoundException {
        return userService.getUserByUuid(uuid);
    }

    /**
     * This is a workaround for the fact that Spring does not allow to have two endpoints with the same path but different parameters.
     * So if you want to get a user by email, you have to add the id '0' to the path.
     */
    @GetMapping("/0")
    @PreAuthorize("hasRole('ADMIN')")
    public User getUserById(@RequestParam String email) throws UserNotFoundException {
        return userService.getUserByEmail(email);
    }

    @PostMapping("/{uuid}")
    @PreAuthorize("hasRole('ADMIN')")
    public User updateUser(@PathVariable UUID uuid, @RequestBody UserUpdateRequestDTO userUpdateRequestDTO) {
        return userService.updateUser(uuid, userUpdateRequestDTO);
    }

    @PostMapping("/{uuid}/enable")
    @PreAuthorize("hasRole('ADMIN')")
    public User enableUser(@PathVariable UUID uuid, @RequestParam Boolean enable) throws UserNotFoundException {
        return userService.enableUser(uuid, enable);
    }


}
