package dev.feder.controller;

import dev.feder.dto.request.UserUpdateRequestDTO;
import dev.feder.exceptions.InvalidEmailException;
import dev.feder.exceptions.UserNotFoundInDbException;
import dev.feder.exceptions.WrongPasswordException;
import dev.feder.model.User;
import dev.feder.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public User getCurrentUser() {
        return userService.getCurrentUser();
    }

    @PostMapping("/me")
    public User updateUser(@RequestBody UserUpdateRequestDTO userUpdateRequestDTO) throws UserNotFoundInDbException, InvalidEmailException, WrongPasswordException {
        return userService.updateUser(userUpdateRequestDTO);
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


}
