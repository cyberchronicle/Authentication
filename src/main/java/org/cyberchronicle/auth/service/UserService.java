package org.cyberchronicle.auth.service;

import lombok.RequiredArgsConstructor;
import org.cyberchronicle.auth.dto.LoginRequest;
import org.cyberchronicle.auth.dto.RegisterRequest;
import org.cyberchronicle.auth.model.User;
import org.cyberchronicle.auth.model.UserRole;
import org.cyberchronicle.auth.repository.UserRepository;
import org.cyberchronicle.auth.repository.UserRoleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public User findById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    @Transactional
    public User register(RegisterRequest registerRequest) {
        var passwordHash = passwordEncoder.encode(registerRequest.password());
        if (userRepository.findByLogin(registerRequest.login()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is already registered");
        }

        var savedUser = userRepository.save(
                User.builder()
                        .firstName(registerRequest.firstName())
                        .lastName(registerRequest.lastName())
                        .login(registerRequest.login())
                        .password(passwordHash)
                        .build()
        );

        var role = UserRole.builder()
                .userId(savedUser.getId())
                .role("USER")
                .build();

        userRoleRepository.save(role);
        return savedUser;
    }

    public User login(LoginRequest loginRequest) {
        var user = userRepository.findByLogin(loginRequest.login())
                .orElseThrow(this::throwUserNotFound);
        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid passport");
        }
        return user;
    }

    public List<UserRole> fetchRoles(Long userId) {
        checkUserExists(userId);
        return userRoleRepository.findByUserId(userId);
    }

    public void addRole(Long userId, String role) {
        checkUserExists(userId);
        var roleView = role.toUpperCase();
        var hasRole = userRoleRepository.findByUserId(userId)
                .stream()
                .map(UserRole::getRole)
                .anyMatch(x -> x.equals(roleView));
        if (hasRole) {
            return;
        }
        userRoleRepository.save(UserRole.builder().userId(userId).role(roleView).build());
    }

    public void revokeRole(Long userId, String role) {
        checkUserExists(userId);
        userRoleRepository.findByUserId(userId)
                .stream()
                .filter(x -> x.getRole().equals(role))
                .findAny()
                .ifPresent(currentRole -> {
                    userRoleRepository.deleteById(currentRole.getId());
                });
    }

    private void checkUserExists(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw throwUserNotFound();
        }
    }

    private ResponseStatusException throwUserNotFound() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    }
}
