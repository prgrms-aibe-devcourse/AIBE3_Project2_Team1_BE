package com.hotsix.server.user.service;

import com.hotsix.server.global.exception.ApplicationException;
import com.hotsix.server.user.dto.UserPasswordChangeRequestDto;
import com.hotsix.server.user.dto.UserUpdateRequestDto;
import com.hotsix.server.user.entity.Role;
import com.hotsix.server.user.entity.User;
import com.hotsix.server.user.exception.UserErrorCase;
import com.hotsix.server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User signUp(String email,
                       String password,
                       LocalDate birthdate,
                       String name,
                       String nickname,
                       String phoneNumber) {
        userRepository.findByEmail(email)
                .ifPresent(_user -> {
                    throw new ApplicationException(UserErrorCase.EMAIL_ALREADY_EXISTS);
                });
        userRepository.findByNickname(nickname)
                .ifPresent(_user -> {
                    throw new ApplicationException(UserErrorCase.NICKNAME_ALREADY_EXISTS);
                });
        Role userRole = Role.CLIENT;
        password = passwordEncoder.encode(password);
        User user = new User(
                email,
                password,
                birthdate,
                name,
                nickname,
                phoneNumber,
                userRole
        );
        return userRepository.save(user);
    }
    @Transactional
    public User updateUser(Long userId, UserUpdateRequestDto dto, User loginUser) {
        if (!userId.equals(loginUser.getUserId())) {
            throw new ApplicationException(UserErrorCase.NO_PERMISSION);
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(UserErrorCase.EMAIL_NOT_FOUND));
        user.update(dto.name(), dto.nickname(), dto.phoneNumber(), dto.birthDate());
        return user;
    }


    @Transactional
    public void changePassword(Long userId, UserPasswordChangeRequestDto dto, User loginUser) {
        if (!userId.equals(loginUser.getUserId())) {
            throw new ApplicationException(UserErrorCase.NO_PERMISSION);
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(UserErrorCase.EMAIL_NOT_FOUND));
        if (!passwordEncoder.matches(dto.currentPassword(), user.getPassword())) {
            throw new ApplicationException(UserErrorCase.INVALID_PASSWORD);
        }
        user.updatePassword(passwordEncoder.encode(dto.newPassword()));
    }

    public void deleteUser(Long userId, User loginUser) {
        if (!userId.equals(loginUser.getUserId())) {
            throw new ApplicationException(UserErrorCase.NO_PERMISSION);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(UserErrorCase.EMAIL_NOT_FOUND));

        userRepository.delete(user);
    }
}
