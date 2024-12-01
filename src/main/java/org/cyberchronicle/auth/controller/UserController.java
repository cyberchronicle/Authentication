package org.cyberchronicle.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.cyberchronicle.auth.dto.LoginRequest;
import org.cyberchronicle.auth.dto.RegisterRequest;
import org.cyberchronicle.auth.dto.RegisterResponse;
import org.cyberchronicle.auth.dto.TokenResponse;
import org.cyberchronicle.auth.dto.UserInfo;
import org.cyberchronicle.auth.model.UserRole;
import org.cyberchronicle.auth.service.TokenService;
import org.cyberchronicle.auth.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;
    private final TokenService tokenService;

    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public RegisterResponse register(@Valid @RequestBody RegisterRequest registerRequest) {
        var user = userService.register(registerRequest);
        var tokens = tokenService.issueNewTokens(user.getId());
        return new RegisterResponse(user.getId(), tokens);
    }

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public TokenResponse login(@RequestBody LoginRequest loginRequest) {
        var user = userService.login(loginRequest);
        return tokenService.issueNewTokens(user.getId());
    }

    @GetMapping(value = "/userinfo", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserInfo userinfo(@RequestParam Long userId) {
        var user = userService.findById(userId);
        return new UserInfo(
                userId,
                user.getFirstName(),
                user.getLastName(),
                user.getLogin(),
                userService.fetchRoles(userId).stream().map(UserRole::getRole).toList()
        );
    }

    @PutMapping("/role")
    public void addRole(@RequestParam Long userId, @RequestParam String role) {
        userService.addRole(userId, role);
    }

    @DeleteMapping("/role")
    public void revokeRole(@RequestParam Long userId, @RequestParam String role) {
        userService.revokeRole(userId, role);
    }
}
