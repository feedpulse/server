package de.feedpulse.controller;

import de.feedpulse.dto.request.AccountRequestDTO;
import de.feedpulse.dto.response.JwtResponseDTO;
import de.feedpulse.model.User;
import de.feedpulse.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Value("${feedpulse.server.deployment.production}")
    private boolean production;

    private final AuthService userService;

    public AuthController(AuthService userService) {
        this.userService = userService;
    }

    @PostMapping(value = {"/signin", "/login"})
    @ResponseStatus(HttpStatus.OK)
    public JwtResponseDTO login(@RequestBody Map<String, String> params, HttpServletResponse response) {
        String jwt = userService.loginUser(params);
        response.addHeader("Authorization", "Bearer " + jwt);
        return new JwtResponseDTO(jwt);
    }


    @PostMapping(value = {"/signup", "/register"})
    @ResponseStatus(HttpStatus.CREATED)
    public User register(@RequestBody Map<String, String> params) {
        if (production) {
            // no registration in production mode for now
            throw new UnsupportedOperationException("Registration is not supported in production mode");
        }
        return userService.createUser(params);
    }
    @PostMapping("request-account")
    @ResponseStatus(HttpStatus.OK)
    public void requestAccount(@RequestBody AccountRequestDTO accountRequestDTO) {
        userService.requestAccount(accountRequestDTO);
    }

}
