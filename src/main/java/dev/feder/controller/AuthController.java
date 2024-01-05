package dev.feder.controller;

import dev.feder.model.User;
import dev.feder.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = {"/signin", "/login"})
    @ResponseStatus(HttpStatus.OK)
    public String login(@RequestBody Map<String, String> params) {
        return userService.loginUser(params);
    }


    @PostMapping(value = {"/signup", "/register"})
    @ResponseStatus(HttpStatus.CREATED)
    public User register(@RequestBody Map<String, String> params) {
        return userService.createUser(params);
    }

}
