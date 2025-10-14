package com.hotsix.server.auth.controller;

import com.hotsix.server.auth.entity.RefreshToken;
import com.hotsix.server.auth.exception.AuthErrorCase;
import com.hotsix.server.auth.service.AuthService;
import com.hotsix.server.global.Rq.Rq;
import com.hotsix.server.global.exception.ApplicationException;
import com.hotsix.server.global.rsData.RsData;
import com.hotsix.server.user.dto.UserDto;
import com.hotsix.server.user.dto.UserLoginRequestDto;
import com.hotsix.server.user.dto.UserLoginResponseDto;
import com.hotsix.server.user.entity.User;
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

    @PostMapping("/login/basic")
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
        UserLoginResponseDto tokenResponse = authService.login(reqBody.email(), reqBody.password());

        rq.setCookie("apiKey", tokenResponse.apiKey());
        rq.setCookie("accessToken", tokenResponse.accessToken());

        return new RsData<>(
                "200-1",
                "%s님 환영합니다.".formatted(tokenResponse.item().getNickname()),
                tokenResponse
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
        rq.deleteCookie("refreshToken");
        rq.deleteCookie("JSESSIONID");

        return new RsData<>(
                "200-1",
                "로그아웃 되었습니다."
        );
    }

    @PostMapping("/token/reissue")
    @Operation(
            summary = "토큰 재발급",
            description = "리프레시 토큰으로 액세스 토큰을 재발급합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "토큰 재발급 성공", content = @Content(schema = @Schema(implementation = UserLoginResponseDto.class))),
                    @ApiResponse(responseCode = "401", description = "리프레시 토큰 유효하지 않음")
            }
    )
    public RsData<UserLoginResponseDto> reissueAccessToken(
            @CookieValue(name = "refreshToken", required = false) String refreshToken
    ) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new ApplicationException(AuthErrorCase.INVALID_REFRESH_TOKEN);
        }

        String newAccessToken = authService.reissueAccessToken(refreshToken);
        RefreshToken tokenEntity = authService.getRefreshToken(refreshToken);

        User user = authService.findById(tokenEntity.getUserId())
                .orElseThrow(() -> new ApplicationException(AuthErrorCase.UNAUTHORIZED));

        UserLoginResponseDto response = new UserLoginResponseDto(
                new UserDto(user),
                user.getApiKey(),
                newAccessToken
        );

        rq.setCookie("accessToken", newAccessToken);

        return new RsData<>(
                "200-2",
                "토큰이 재발급되었습니다.",
                response
        );
    }
}