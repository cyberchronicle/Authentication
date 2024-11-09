package org.cyberchronicle.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

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
                .orElseThrow(UserNotFoundException::new);
        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new InvalidPasswordException();
        }
        return user;
    }

    public List<UserRole> fetchRoles(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new UserNotFoundException();
        }
        return userRoleRepository.findByUserId(userId);
    }
}
