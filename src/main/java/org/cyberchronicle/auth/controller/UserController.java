package org.cyberchronicle.auth.controller;

import lombok.RequiredArgsConstructor;
import org.cyberchronicle.auth.dto.LoginRequest;
import org.cyberchronicle.auth.dto.RegisterRequest;
import org.cyberchronicle.auth.dto.TokenResponse;
import org.cyberchronicle.auth.dto.UserInfo;
import org.cyberchronicle.auth.service.TokenService;
import org.cyberchronicle.auth.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final TokenService tokenService;

    @PostMapping("/register")
    public TokenResponse register(@RequestBody RegisterRequest registerRequest) {
        var user = userService.register(registerRequest);
        return tokenService.issueNewTokens(user.getId());
    }

    @PostMapping("/login")
    public TokenResponse login(@RequestBody LoginRequest loginRequest) {
        var user = userService.login(loginRequest);
        return tokenService.issueNewTokens(user.getId());
    }

    @GetMapping("/userinfo")
    public UserInfo userinfo(@RequestParam Long userId) {
        var user = userService.findById(userId);
        return new UserInfo(
                user.getFirstName(),
                user.getLastName(),
                user.getLogin()
        );
    }

    @PutMapping("/role")
    public void addRole(@RequestParam UUID userId, @RequestParam String role) {

    }

    @DeleteMapping("/role")
    public void revokeRole(@RequestParam UUID userId, @RequestParam String role) {

    }
}
