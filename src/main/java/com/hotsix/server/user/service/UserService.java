package com.hotsix.server.user.service;

import com.hotsix.server.auth.service.AuthService;
import com.hotsix.server.global.exception.ServiceException;
import com.hotsix.server.profile.entity.Profile;
import com.hotsix.server.user.entity.User;
import com.hotsix.server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    public User signUp(String email, String password, String nickname) {
        userRepository.findByEmail(email)
                .ifPresent(_user -> {
                    throw new ServiceException("409-1", "이미 존재하는 회원입니다.");
                });

        password = passwordEncoder.encode(password);

        User user = new User(email, password, nickname, null);

        return userRepository.save(user);
    }

    public User signUp(String email, String password, String nickname, Profile profile) {
        userRepository.findByEmail(email)
                .ifPresent(_user -> {
                    throw new ServiceException("409-1", "이미 존재하는 회원입니다.");
                });

        password = (password != null && !password.isBlank()) ?  passwordEncoder.encode(password) : null;

        User user = new User(email, password, nickname, profile);

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
            throw new ServiceException("401-1", "비밀번호가 일치 하지 않습니다.");
        }
    }
    
    
}
