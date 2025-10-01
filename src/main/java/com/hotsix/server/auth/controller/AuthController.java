package com.hotsix.server.auth.controller;

import com.hotsix.server.auth.service.AuthService;
import com.hotsix.server.global.Rq.Rq;
import com.hotsix.server.global.exception.ApplicationException;
import com.hotsix.server.global.rsData.RsData;
import com.hotsix.server.user.dto.UserDto;
import com.hotsix.server.user.dto.UserLoginRequestDto;
import com.hotsix.server.user.dto.UserLoginResponseDto;
import com.hotsix.server.user.entity.User;
import com.hotsix.server.user.exception.UserErrorCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final Rq rq;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @PostMapping("/basic/login")
    @Operation(
            summary = "로그인",
            description = "이메일과 비밀번호로 로그인합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(schema = @Schema(implementation = UserLoginResponseDto.class))),
                    @ApiResponse(responseCode = "401", description = "존재하지 않는 회원 또는 비밀번호 불일치")
            }
    )
    public RsData<UserLoginResponseDto> login(
            @Valid @RequestBody UserLoginRequestDto reqBody
    ) {
        User user = authService.findByEmail(reqBody.email())
                .orElseThrow(() -> new ApplicationException(UserErrorCase.EMAIL_NOT_FOUND));

        authService.checkPassword(user, reqBody.password());

        String accessToken = authService.genAccessToken(user);

        rq.setCookie("apiKey", user.getApiKey());
        rq.setCookie("accessToken", accessToken);

        return new RsData<>(
                "200-1",
                "%s님 환영합니다.".formatted(user.getNickname()),
                new UserLoginResponseDto(
                        new UserDto(user),
                        user.getApiKey(),
                        accessToken
                )
        );
    }

    @Transactional
    @DeleteMapping("/logout")
    @Operation(
            summary = "로그아웃",
            description = "로그아웃 처리 및 쿠키 삭제",
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그아웃 성공")
            }
    )
    public RsData<Void> logout() {
        rq.deleteCookie("apiKey");
        rq.deleteCookie("accessToken");

        return new RsData<>(
                "200-1",
                "로그아웃 되었습니다."
        );
    }
}
