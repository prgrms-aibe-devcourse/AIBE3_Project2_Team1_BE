package com.hotsix.server.user.service;

import com.hotsix.server.auth.service.AuthService;
import com.hotsix.server.global.exception.ApplicationException;
import com.hotsix.server.user.entity.Role;
import com.hotsix.server.user.entity.User;
import com.hotsix.server.user.exception.UserErrorCase;
import com.hotsix.server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final AuthService authService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public long count() {
        return userRepository.count();
    }

    public User signUp(String email, String password, LocalDate birthdate, String name, String nickname, String phoneNumber, Role role) {
        userRepository.findByEmail(email)
                .ifPresent(_user -> {
                    throw new ApplicationException(UserErrorCase.EMAIL_ALREADY_EXISTS);
                });

        if (role == null) {
            role = Role.CLIENT; // 기본값 지정
        }
        password = passwordEncoder.encode(password);

        User user = new User(email, password, birthdate, name, nickname, phoneNumber, role);

        return userRepository.save(user);
    }


    public Optional<User> findByApiKey(String apiKey) {
        return userRepository.findByApiKey(apiKey);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public String genAccessToken(User user) {
        return authService.genAccessToken(user);
    }

    public Map<String, Object> payload(String accessToken) {
        return authService.payload(accessToken);
    }

    public Optional<User> findById(long id) {
        return userRepository.findById(id);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void checkPassword(User user, String password) {
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ApplicationException(UserErrorCase.INVALID_PASSWORD);
        }
    }
    
    
}
