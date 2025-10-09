package com.hotsix.server.user.controller;

import com.hotsix.server.global.Rq.Rq;
import com.hotsix.server.global.rsData.RsData;
import com.hotsix.server.user.dto.UserDto;
import com.hotsix.server.user.dto.UserPasswordChangeRequestDto;
import com.hotsix.server.user.dto.UserRegisterRequestDto;
import com.hotsix.server.user.dto.UserUpdateRequestDto;
import com.hotsix.server.user.entity.User;
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

    @GetMapping("/info")
    @Operation(
            summary = "내 정보 조회",
            description = "현재 로그인한 사용자의 정보를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = UserDto.class))),
                    @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
            }
    )
    public RsData<UserDto> getMyInfo() {
        User currentUser = rq.getUser();

        return new RsData<>(
                "200-5",
                "내 정보 조회 성공",
                new UserDto(currentUser)
        );
    }

    @Transactional
    @PutMapping("/info")
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
            @Valid @RequestBody UserUpdateRequestDto reqBody
    ) {
        Long userId = rq.getUser().getUserId();

        User updatedUser = userService.updateUser(userId, reqBody, rq.getUser());

        return new RsData<>(
                "200-3",
                "회원정보가 수정되었습니다.",
                new UserDto(updatedUser)
        );
    }

    @Transactional
    @PatchMapping("/info/password")
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
            @Valid @RequestBody UserPasswordChangeRequestDto reqBody
    ) {
        Long userId = rq.getUser().getUserId();

        userService.changePassword(userId, reqBody, rq.getUser());

        return new RsData<>(
                "200-4",
                "비밀번호가 변경되었습니다."
        );
    }

    @Transactional
    @DeleteMapping
    @Operation(
            summary = "회원탈퇴",
            description = "본인 계정의 회원탈퇴를 처리합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "회원탈퇴 성공"),
                    @ApiResponse(responseCode = "403", description = "권한 없음"),
                    @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음")
            }
    )
    public RsData<Void> deleteUser() {
        Long userId = rq.getUser().getUserId();

        userService.deleteUser(userId, rq.getUser());

        // 쿠키 삭제
        rq.deleteCookie("apiKey");
        rq.deleteCookie("accessToken");

        return new RsData<>(
                "200-2",
                "회원탈퇴가 완료되었습니다."
        );
    }
}
