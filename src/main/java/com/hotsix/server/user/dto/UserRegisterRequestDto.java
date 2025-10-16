package com.hotsix.server.user.dto;

import com.hotsix.server.user.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record UserRegisterRequestDto(
        @Email
        @Schema(description = "이메일", example = "test@example.com")
        String email,

        @NotBlank(message = "비밀번호는 필수 입력값입니다.")
        @Schema(description = "비밀번호", example = "password123!")
        String password,

        @Schema(description = "이름", example = "홍길동")
        String name,

        @Schema(description = "닉네임", example = "gildong")
        String nickname,

        @Schema(description = "전화번호", example = "010-1234-5678")
        String phoneNumber,

        @Schema(description = "생년월일", example = "2000-01-01")
        LocalDate birthDate,

        @Schema(description = "역할 (CLIENT 또는 FREELANCER)", example = "CLIENT", allowableValues = {"CLIENT", "FREELANCER"}, defaultValue = "CLIENT")
        Role role

) {}