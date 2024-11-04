package org.cyberchronicle.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SignController {
    private final AuthService authService;

    @GetMapping("/sign")
    public String sign(@RequestParam("login") String login) {
        return authService.issueToken(login);
    }
}
