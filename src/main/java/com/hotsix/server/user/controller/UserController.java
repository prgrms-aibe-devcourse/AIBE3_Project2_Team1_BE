package com.hotsix.server.user.controller;

import com.hotsix.server.global.Rq.Rq;
import com.hotsix.server.global.exception.ApplicationException;
import com.hotsix.server.global.rsData.RsData;
import com.hotsix.server.user.dto.*;
import com.hotsix.server.user.entity.User;
import com.hotsix.server.user.exception.UserErrorCase;
import com.hotsix.server.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
        User user = userService.signUp(reqBody.email(), reqBody.password(), reqBody.birthDate(), reqBody.name(), reqBody.nickname(), reqBody.phoneNumber(), reqBody.role());

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
                .orElseThrow(() -> new ApplicationException(UserErrorCase.EMAIL_NOT_FOUND));

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

    @Transactional
    @PutMapping("/{id}")
    @Operation(
            summary = "회원정보 수정",
            description = "본인 계정의 이름, 닉네임, 전화번호, 생년월일을 수정합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "회원정보 수정 성공", content = @Content(schema = @Schema(implementation = UserDto.class))),
                    @ApiResponse(responseCode = "403", description = "권한 없음"),
                    @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음")
            }
    )
    public RsData<UserDto> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequestDto reqBody
    ) {
        User updatedUser = userService.updateUser(id, reqBody, rq.getUser());

        return new RsData<>(
                "200-3",
                "회원정보가 수정되었습니다.",
                new UserDto(updatedUser)
        );
    }

    @Transactional
    @PatchMapping("/{id}/password")
    @Operation(
            summary = "비밀번호 변경",
            description = "본인 계정의 현재 비밀번호를 확인한 후 새 비밀번호로 변경합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "비밀번호 변경 성공"),
                    @ApiResponse(responseCode = "400", description = "현재 비밀번호 불일치"),
                    @ApiResponse(responseCode = "403", description = "권한 없음"),
                    @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음")
            }
    )
    public RsData<Void> changePassword(
            @PathVariable Long id,
            @Valid @RequestBody UserPasswordChangeRequestDto reqBody
    ) {
        userService.changePassword(id, reqBody, rq.getUser());

        return new RsData<>(
                "200-4",
                "비밀번호가 변경되었습니다."
        );
    }

    @Transactional
    @DeleteMapping("/{id}")
    @Operation(
            summary = "회원탈퇴",
            description = "본인 계정의 회원탈퇴를 처리합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "회원탈퇴 성공"),
                    @ApiResponse(responseCode = "403", description = "권한 없음"),
                    @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음")
            }
    )
    public RsData<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id, rq.getUser());

        // 쿠키 삭제
        rq.deleteCookie("apiKey");
        rq.deleteCookie("accessToken");

        return new RsData<>(
                "200-2",
                "회원탈퇴가 완료되었습니다."
        );
    }
}
