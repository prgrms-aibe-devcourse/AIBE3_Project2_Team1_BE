package com.hotsix.server.profile.service;

import com.hotsix.server.profile.dto.ProfileUpdateRequestDto;
import com.hotsix.server.profile.entity.Profile;
import com.hotsix.server.profile.repository.ProfileRepository;
import com.hotsix.server.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;

    @Transactional(readOnly = true)
    public Profile getProfileByUser(User user) {
        return profileRepository.findByUserWithUser(user)
                .orElseThrow(() -> new RuntimeException("프로필을 찾을 수 없습니다."));
    }

    @Transactional
    public Profile updateProfile(User user, ProfileUpdateRequestDto dto) {
        Profile profile = getProfileByUser(user);

        profile.setTitle(dto.title());
        profile.setDescription(dto.description());
        profile.setSkills(dto.skills());
        profile.setHourlyRate(dto.hourlyRate());
        profile.setVisibility(dto.visibility());

        return profileRepository.save(profile);
    }
}
