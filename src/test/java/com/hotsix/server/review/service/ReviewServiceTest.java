package com.hotsix.server.review.service;

import com.hotsix.server.project.repository.ProjectRepository;
import com.hotsix.server.review.repository.ReviewImageRepository;
import com.hotsix.server.review.repository.ReviewRepository;

import com.hotsix.server.global.exception.ApplicationException;
import com.hotsix.server.project.entity.Project;
import com.hotsix.server.project.entity.Status;
import com.hotsix.server.review.dto.ReviewRequestDto;
import com.hotsix.server.review.entity.Review;
import com.hotsix.server.review.entity.ReviewImage;
import com.hotsix.server.review.exception.ReviewErrorCase;
import com.hotsix.server.user.entity.User;
import com.hotsix.server.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ActiveProfiles("test")
class ReviewServiceTest {

    private ReviewRepository reviewRepository;
    private ReviewImageRepository reviewImageRepository;
    private UserRepository userRepository;
    private ProjectRepository projectRepository;

    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        reviewRepository = mock(ReviewRepository.class);
        reviewImageRepository = mock(ReviewImageRepository.class);
        userRepository = mock(UserRepository.class);
        projectRepository = mock(ProjectRepository.class);

        reviewService = new ReviewService(
                reviewRepository,
                reviewImageRepository,
                userRepository,
                projectRepository
        );
    }

    @Nested
    @DisplayName("리뷰 등록 테스트")
    class RegisterReview {

        private final Long fromUserId = 1L;
        private final Long projectId = 100L;

        private final User fromUser = User.builder().userId(fromUserId).nickname("유저1").build();
        private final User toUser = User.builder().userId(2L).nickname("유저2").build();

        private final Project project = Project.builder()
                .projectId(projectId)
                .initator(fromUser) // fromUser가 client
                .participant(toUser) // toUser가 freelancer
                .title("테스트 프로젝트")
                .description("테스트")
                .budget(1000)
                .deadline(LocalDate.now())
                .status(Status.COMPLETED) // 완료 상태
                .category("디자인")
                .build();

        @Test
        @DisplayName("리뷰 등록 성공")
        void registerReviewSuccess() {
            ReviewRequestDto dto = new ReviewRequestDto(
                    projectId,
                    BigDecimal.valueOf(4.5),
                    "좋은 프로젝트였습니다. 감사합니다!",
                    List.of("https://s3.aws.com/image1.png")
            );

            given(projectRepository.findById(projectId)).willReturn(Optional.of(project));
            given(userRepository.findById(fromUserId)).willReturn(Optional.of(fromUser));
            given(reviewRepository.existsByProject_ProjectIdAndFromUser_UserId(projectId, fromUserId)).willReturn(false);
            given(reviewRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));
            given(reviewImageRepository.saveAll(anyList())).willAnswer(invocation -> invocation.getArgument(0));

            reviewService.registerReview(fromUserId, dto);

            ArgumentCaptor<Review> captor = ArgumentCaptor.forClass(Review.class);
            verify(reviewRepository, times(1)).save(captor.capture());

            Review saved = captor.getValue();
            assertThat(saved.getRating()).isEqualByComparingTo("4.5");
            assertThat(saved.getComment()).isEqualTo("좋은 프로젝트였습니다. 감사합니다!");
            assertThat(saved.getToUser().getUserId()).isEqualTo(toUser.getUserId());

            verify(reviewImageRepository).saveAll(anyList());
        }

        @Test
        @DisplayName("평점 범위 벗어남 - 예외 발생")
        void registerReviewInvalidRating() {
            // 별점 범위 밖으로 설정
            ReviewRequestDto dto = new ReviewRequestDto(
                    projectId,
                    BigDecimal.valueOf(6.0),
                    "평점이 너무 높음",
                    List.of("https://s3.aws.com/image1.png")
            );

            given(projectRepository.findById(projectId)).willReturn(Optional.of(project));
            given(userRepository.findById(fromUserId)).willReturn(Optional.of(fromUser));

            assertThatThrownBy(() -> reviewService.registerReview(fromUserId, dto))
                    .isInstanceOf(ApplicationException.class)
                    .hasMessageContaining(ReviewErrorCase.INVALID_RATING.getMessage());

            verify(reviewRepository, never()).save(any());
        }

        @Test
        @DisplayName("존재하지 않는 프로젝트 ID")
        void registerReviewInvalidProject() {
            ReviewRequestDto dto = new ReviewRequestDto(
                    999L, // 존재하지 않는 프로젝트 ID 설정
                    BigDecimal.valueOf(4),
                    "프로젝트 없음",
                    List.of("https://s3.aws.com/image1.png")
            );

            given(projectRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> reviewService.registerReview(fromUserId, dto))
                    .isInstanceOf(ApplicationException.class);

            verify(reviewRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("리뷰 조회 테스트")
    class GetReview {

        @Test
        @DisplayName("내가 쓴 리뷰 리스트 조회 성공")
        void getReviewListSuccess() throws Exception {
            Long fromUserId = 1L;

            Review review = Review.builder()
                    .reviewId(1L)
                    .fromUser(User.builder().userId(fromUserId).nickname("작성자").build())
                    .toUser(User.builder().userId(2L).nickname("상대방").build())
                    .comment("굿굿")
                    .rating(BigDecimal.valueOf(5))
                    .reviewImageList(List.of(
                            ReviewImage.of(null, "https://cdn.s3/image1.png"),
                            ReviewImage.of(null, "https://cdn.s3/image2.png")
                    ))
                    .build();

            Field createdAtField = Review.class.getSuperclass().getDeclaredField("createdAt");
            createdAtField.setAccessible(true);
            createdAtField.set(review, LocalDateTime.now());

            given(reviewRepository.findAllByFromUser_UserId(fromUserId)).willReturn(List.of(review));

            var result = reviewService.getReviewsWrittenByUser(fromUserId);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).comment()).contains("굿굿");
            assertThat(result.get(0).images()).containsExactly(
                    "https://cdn.s3/image1.png", "https://cdn.s3/image2.png"
            );
        }

        @Test
        @DisplayName("내가 쓴 리뷰 없음 = 빈 리스트 반환")
        void getEmptyReviewList() {
            Long userId = 3L;
            given(reviewRepository.findAllByFromUser_UserId(userId)).willReturn(List.of());

            var result = reviewService.getReviewsWrittenByUser(userId);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("리뷰 수정 테스트")
    class UpdateReview {

        private final Long userId = 1L;
        private final Long reviewId = 100L;
        private Review review;

        @BeforeEach
        void init() {
            review = Review.builder()
                    .reviewId(reviewId)
                    .fromUser(User.builder().userId(userId).nickname("작성자").build())
                    .toUser(User.builder().userId(2L).nickname("상대방").build())
                    .comment("이전 코멘트")
                    .rating(BigDecimal.valueOf(4.0))
                    .reviewImageList(new ArrayList<>(List.of(
                            ReviewImage.of(null, "https://old-image.com/1.jpg")
                    )))
                    .build();
        }

        @Test
        @DisplayName("리뷰 수정 성공")
        void updateReviewSuccess() {
            ReviewRequestDto dto = new ReviewRequestDto(
                    1L,
                    BigDecimal.valueOf(5.0),
                    "수정된 코멘트",
                    List.of("https://new-image.com/1.jpg", "https://new-image.com/2.jpg")
            );

            given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));
            given(reviewRepository.save(any())).willAnswer(inv -> inv.getArgument(0));

            reviewService.updateReview(userId, reviewId, dto);

            verify(reviewImageRepository).deleteAll(anyList());
            verify(reviewRepository).save(any());
        }

        @Test
        @DisplayName("리뷰 수정 실패 - 작성자가 아님")
        void updateReviewUnauthorized() {
            ReviewRequestDto dto = new ReviewRequestDto(
                    1L,
                    BigDecimal.valueOf(5.0),
                    "수정된 코멘트",
                    List.of("https://new-image.com/1.jpg")
            );

            review = Review.builder()
                    .reviewId(reviewId)
                    .fromUser(User.builder().userId(999L).nickname("다른사람").build())
                    .toUser(User.builder().userId(userId).build())
                    .comment("이전")
                    .rating(BigDecimal.valueOf(4.0))
                    .reviewImageList(List.of())
                    .build();

            given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));

            assertThatThrownBy(() -> reviewService.updateReview(userId, reviewId, dto))
                    .isInstanceOf(ApplicationException.class)
                    .hasMessageContaining(ReviewErrorCase.UNAUTHORIZED_REVIEWER.getMessage());
        }
    }

    @Nested
    @DisplayName("리뷰 삭제 테스트")
    class DeleteReview {

        private final Long userId = 1L;
        private final Long reviewId = 200L;

        @Test
        @DisplayName("리뷰 삭제 성공")
        void deleteReviewSuccess() {
            Review review = Review.builder()
                    .reviewId(reviewId)
                    .fromUser(User.builder().userId(userId).nickname("작성자").build())
                    .toUser(User.builder().userId(2L).nickname("상대방").build())
                    .build();

            given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));

            reviewService.deleteReview(userId, reviewId);

            verify(reviewRepository).delete(review);
        }

        @Test
        @DisplayName("리뷰 삭제 실패 - 작성자가 아님")
        void deleteReviewUnauthorized() {
            Review review = Review.builder()
                    .reviewId(reviewId)
                    .fromUser(User.builder().userId(999L).nickname("다른사람").build())
                    .toUser(User.builder().userId(userId).build())
                    .build();

            given(reviewRepository.findById(reviewId)).willReturn(Optional.of(review));

            assertThatThrownBy(() -> reviewService.deleteReview(userId, reviewId))
                    .isInstanceOf(ApplicationException.class)
                    .hasMessageContaining(ReviewErrorCase.UNAUTHORIZED_REVIEWER.getMessage());

            verify(reviewRepository, never()).delete(any());
        }

        @Test
        @DisplayName("리뷰 삭제 실패 - 존재하지 않는 리뷰")
        void deleteReviewNotFound() {
            given(reviewRepository.findById(reviewId)).willReturn(Optional.empty());

            assertThatThrownBy(() -> reviewService.deleteReview(userId, reviewId))
                    .isInstanceOf(ApplicationException.class)
                    .hasMessageContaining(ReviewErrorCase.REVIEW_NOT_FOUND.getMessage());
        }
    }
}