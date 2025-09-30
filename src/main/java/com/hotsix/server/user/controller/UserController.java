package com.hotsix.server.user.controller;

import com.hotsix.server.global.Rq.Rq;
import com.hotsix.server.global.exception.ServiceException;
import com.hotsix.server.global.rsData.RsData;
import com.hotsix.server.user.dto.UserDto;
import com.hotsix.server.user.dto.UserLoginRequestDto;
import com.hotsix.server.user.dto.UserLoginResponeDto;
import com.hotsix.server.user.dto.UserRegisterRequestDto;
import com.hotsix.server.user.entity.User;
import com.hotsix.server.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User API", description = "유저 관련 API 컨트롤러")
public class UserController {
    private final UserService userService;
    private final Rq rq;

    @Transactional
    @PostMapping("/signup")
    @Operation(
            summary = "회원가입",
            description = "새로운 유저를 생성합니다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "회원가입 성공", content = @Content(schema = @Schema(implementation = UserDto.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
            }
    )
    public RsData<UserDto> join(@Valid @RequestBody UserRegisterRequestDto reqBody) {
        User user = userService.signUp(reqBody.name(), reqBody.password(), reqBody.nickname());

        return new RsData<>(
                "201-1",
                "%s님 환영합니다. 회원가입이 완료되었습니다.".formatted(user.getNickname()),
                new UserDto(user)
        );
    }

    @Transactional
    @PostMapping("/login")
    @Operation(
            summary = "로그인",
            description = "이메일과 비밀번호로 로그인합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(schema = @Schema(implementation = UserLoginResponeDto.class))),
                    @ApiResponse(responseCode = "401", description = "존재하지 않는 회원 또는 비밀번호 불일치")
            }
    )
    public RsData<UserLoginResponeDto> login(
            @Valid @RequestBody UserLoginRequestDto reqBody
    ) {
        User user = userService.findByEmail(reqBody.email())
                .orElseThrow(() -> new ServiceException("401-1", "존재하지 않는 회원입니다."));

        userService.checkPassword(user, reqBody.password());

        String accessToken = userService.genAccessToken(user);

        rq.setCookie("apiKey", user.getApiKey());
        rq.setCookie("accessToken", accessToken);

        return new RsData<>(
                "200-1",
                "%s님 환영합니다.".formatted(user.getNickname()),
                new UserLoginResponeDto(
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
