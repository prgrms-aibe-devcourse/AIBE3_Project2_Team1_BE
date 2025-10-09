package com.hotsix.server.milestone.dto;

import com.hotsix.server.milestone.entity.Milestone;
import com.hotsix.server.project.entity.Project;
import com.hotsix.server.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MilestoneResponseDto {
    private Long milestoneId;
    private String title;
    private String description;
    private String dueDate;
    private String status;

    // 팀원 정보 추가!
    private List<MemberInfo> members;

    public static MilestoneResponseDto from(Milestone milestone) {
        // 팀원 정보 가져오기
        List<MemberInfo> members = new ArrayList<>();

        try {
            Project project = milestone.getContract()
                    .getProposal()
                    .getProject();

            // 클라이언트
            User client = project.getClient();
            if (client != null) {
                members.add(MemberInfo.builder()
                        .userId(client.getUserId())
                        .name(client.getName())
                        .nickname(client.getNickname())
                        .role("CLIENT")
                        .build());
            }

            // 프리랜서
            User freelancer = project.getFreelancer();
            if (freelancer != null) {
                members.add(MemberInfo.builder()
                        .userId(freelancer.getUserId())
                        .name(freelancer.getName())
                        .nickname(freelancer.getNickname())
                        .role("FREELANCER")
                        // .imageUrl(freelancer.getProfileImageUrl()) //대표 사진
                        .build());
            }
        } catch (Exception e) {
            // Contract나 Proposal이 없을 경우 빈 리스트
        }

        return MilestoneResponseDto.builder()
                .milestoneId(milestone.getMilestoneId())
                .title(milestone.getTitle())
                .description(milestone.getDescription())
                .dueDate(milestone.getDueDate() == null ? null : milestone.getDueDate().toString())
                .status(milestone.getMilestoneStatus() == null ? null : milestone.getMilestoneStatus().toString())
                .members(members)
                .build();
    }

    // 내부 클래스: 팀원 정보
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberInfo {
        private Long userId;
        private String name;
        private String nickname;
        private String role;  // "CLIENT" or "FREELANCER"
        // private String imageUrl; // 팀/프리랜서 프로필 이미지(연동전)
    }
}
