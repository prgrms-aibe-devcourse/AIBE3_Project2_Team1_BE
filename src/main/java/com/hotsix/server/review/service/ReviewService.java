package com.hotsix.server.review.service;

import com.hotsix.server.global.exception.ApplicationException;
import com.hotsix.server.project.entity.Project;
import com.hotsix.server.project.entity.Status;
import com.hotsix.server.project.repository.ProjectRepository;
import com.hotsix.server.review.dto.ReviewRequestDto;
import com.hotsix.server.review.dto.ReviewResponseDto;
import com.hotsix.server.review.entity.Review;
import com.hotsix.server.review.entity.ReviewImage;
import com.hotsix.server.review.exception.ReviewErrorCase;
import com.hotsix.server.review.repository.ReviewImageRepository;
import com.hotsix.server.review.repository.ReviewRepository;
import com.hotsix.server.user.entity.User;
import com.hotsix.server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    @Transactional
    public void registerReview(Long fromUserId, ReviewRequestDto dto) {
        Project project = projectRepository.findById(dto.projectId())
                .orElseThrow(() -> new ApplicationException(ReviewErrorCase.PROJECT_NOT_FOUND));

        if (!Status.COMPLETED.equals(project.getStatus())) {
            throw new ApplicationException(ReviewErrorCase.PROJECT_NOT_COMPLETED);
        }

        if (dto.rating() == null ||
                dto.rating().compareTo(new BigDecimal("1.0")) < 0 ||
                dto.rating().compareTo(new BigDecimal("5.0")) > 0) {
            throw new ApplicationException(ReviewErrorCase.INVALID_RATING);
        }

        if (reviewRepository.existsByProject_ProjectIdAndFromUser_UserId(project.getProjectId(), fromUserId)) {
            throw new ApplicationException(ReviewErrorCase.REVIEW_ALREADY_EXISTS);
        }

        User fromUser = userRepository.findById(fromUserId)
                .orElseThrow(() -> new ApplicationException(ReviewErrorCase.REVIEW_NOT_FOUND));

        User toUser = getTargetUser(project, fromUser);

        Review review = Review.of(project, fromUser, toUser, dto.rating(), dto.comment());
        reviewRepository.save(review);

        if (dto.images() != null && !dto.images().isEmpty()) {
            dto.images().forEach(img -> reviewImageRepository.save(ReviewImage.of(review, img)));
        }
    }

    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getReviewsWrittenByUser(Long fromUserId) {
        return reviewRepository.findAllByFromUser_UserId(fromUserId).stream()
                .map(review -> new ReviewResponseDto(
                        review.getReviewId(),
                        review.getToUser().getNickname(),
                        review.getRating(),
                        review.getComment(),
                        review.getCreatedAt().toLocalDate(),
                        review.getReviewImageList().stream()
                                .map(ReviewImage::getImageUrl)
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }

    // 프로젝트와 작성자를 기반으로 리뷰 대상 결정
    private User getTargetUser(Project project, User writer) {
        if (project.getClient().equals(writer)) {
                        return project.getFreelancer();
                    }
                if (project.getFreelancer().equals(writer)) {
                        return project.getClient();
                    }
                throw new ApplicationException(ReviewErrorCase.UNAUTHORIZED_REVIEWER);
    }
}