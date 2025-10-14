package com.hotsix.server.profile.controller;

import com.hotsix.server.global.Rq.Rq;
import com.hotsix.server.global.rsData.RsData;
import com.hotsix.server.profile.dto.ProfileResponseDto;
import com.hotsix.server.profile.dto.ProfileUpdateRequestDto;
import com.hotsix.server.profile.entity.Profile;
import com.hotsix.server.profile.service.ProfileService;
import com.hotsix.server.user.entity.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profiles")
@RequiredArgsConstructor
@Tag(name = "Profile API", description = "프로필 관련 API 컨트롤러")
public class ProfileController {

    private final ProfileService profileService;
    private final Rq rq;

    @GetMapping("/me")
    public RsData<ProfileResponseDto> getMyProfile() {
        User currentUser = rq.getUser();
        Profile profile = profileService.getProfileByUser(currentUser);

        return new RsData<>(
                "200-1",
                "내 프로필 조회 성공",
                ProfileResponseDto.from(profile)
        );
    }

    @PutMapping("/me")
    public RsData<ProfileResponseDto> updateMyProfile(@Valid @RequestBody ProfileUpdateRequestDto reqBody) {
        User currentUser = rq.getUser();
        Profile updatedProfile = profileService.updateProfile(currentUser, reqBody);

        return new RsData<>(
                "200-2",
                "프로필이 성공적으로 수정되었습니다.",
                ProfileResponseDto.from(updatedProfile)
        );
    }
}
