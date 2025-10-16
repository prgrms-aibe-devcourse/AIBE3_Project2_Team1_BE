package com.hotsix.server.user.service;

import com.hotsix.server.aws.manager.AmazonS3Manager;
import com.hotsix.server.global.Rq.Rq;
import com.hotsix.server.global.config.security.jwt.JwtTokenProvider;
import com.hotsix.server.global.exception.ApplicationException;
import com.hotsix.server.profile.entity.Profile;
import com.hotsix.server.profile.entity.Visibility;
import com.hotsix.server.profile.repository.ProfileRepository;
import com.hotsix.server.user.dto.UserPasswordChangeRequestDto;
import com.hotsix.server.user.dto.UserUpdateRequestDto;
import com.hotsix.server.user.entity.Provider;
import com.hotsix.server.user.entity.Role;
import com.hotsix.server.user.entity.User;
import com.hotsix.server.user.exception.UserErrorCase;
import com.hotsix.server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AmazonS3Manager amazonS3Manager;
    private final Rq rq;

    @Transactional
    public User signUp(String email,
                       String password,
                       LocalDate birthdate,
                       String name,
                       String nickname,
                       String phoneNumber,
                       Role userRole
    ) {

        userRepository.findByEmail(email)
                .ifPresent(_user -> {
                    throw new ApplicationException(UserErrorCase.EMAIL_ALREADY_EXISTS);
                });

        userRepository.findByNickname(nickname)
                .ifPresent(_user -> {
                    throw new ApplicationException(UserErrorCase.NICKNAME_ALREADY_EXISTS);
                });

        Provider userProvider = Provider.NORMAL;
        password = passwordEncoder.encode(password);

        User user = new User(
                email,
                password,
                birthdate,
                name,
                nickname,
                phoneNumber,
                userRole,
                userProvider
        );

        Profile profile = Profile.builder()
                .title("새로운 사용자")
                .description("아직 프로필이 작성되지 않았습니다.")
                .skills("")
                .hourlyRate(0)
                .visibility(Visibility.PRIVATE)
                .build();

        profile.assignUser(user);

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

    public void save(User user) {
        userRepository.save(user);
    }

    @Transactional
    public String uploadProfileImage(MultipartFile file) {
        User user = rq.getUser();
        if (file.isEmpty()) {
            throw new ApplicationException(UserErrorCase.NO_USER_IMAGE_FILE);
        }

        // S3에 업로드
        String fileUrl = amazonS3Manager.uploadFile(file);

        // DB에 URL 저장
        user.setPicture(fileUrl);
        userRepository.save(user);

        return fileUrl;
    }
}