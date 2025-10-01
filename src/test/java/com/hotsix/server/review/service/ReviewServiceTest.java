package com.hotsix.server.review.service;

import com.hotsix.server.global.exception.ApplicationException;
import com.hotsix.server.review.dto.ReviewRequestDto;
import com.hotsix.server.review.entity.Review;
import com.hotsix.server.review.entity.ReviewImage;
import com.hotsix.server.review.exception.ReviewErrorCase;
import com.hotsix.server.review.repository.ReviewImageRepository;
import com.hotsix.server.review.repository.ReviewRepository;
import com.hotsix.server.user.entity.User;
import com.hotsix.server.user.repository.UserRepository;
import com.hotsix.server.proposal.entity.Contract;
import com.hotsix.server.proposal.repository.ContractRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;

class ReviewServiceTest {

    private ReviewRepository reviewRepository;
    private ReviewImageRepository reviewImageRepository;
    private UserRepository userRepository;
    private ContractRepository contractRepository;

    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        reviewRepository = mock(ReviewRepository.class);
        reviewImageRepository = mock(ReviewImageRepository.class);
        userRepository = mock(UserRepository.class);
        contractRepository = mock(ContractRepository.class);

        reviewService = new ReviewService(
                reviewRepository,
                reviewImageRepository,
                userRepository,
                contractRepository
        );
    }

    @Nested
    @DisplayName("리뷰 등록 테스트")
    class RegisterReview {

        private final Long fromUserId = 1L;
        private final Long toUserId = 2L;
        private final Long contractId = 100L;

        private final User fromUser = User.builder().userId(fromUserId).nickname("유저1").build();
        private final User toUser = User.builder().userId(toUserId).nickname("유저2").build();
        private final Contract contract = Contract.builder().id(contractId).build();

        @Test
        @DisplayName("리뷰 등록 성공")
        void registerReviewSuccess() {
            ReviewRequestDto dto = new ReviewRequestDto(
                    contractId,
                    toUserId,
                    BigDecimal.valueOf(4.5),
                    "좋은 프로젝트였습니다. 감사합니다!",
                    List.of("https://s3.aws.com/image1.png")
            );

            given(userRepository.findById(fromUserId)).willReturn(Optional.of(fromUser));
            given(userRepository.findById(toUserId)).willReturn(Optional.of(toUser));
            given(contractRepository.findById(contractId)).willReturn(Optional.of(contract));
            given(reviewRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));

            reviewService.registerReview(fromUserId, dto);

            ArgumentCaptor<Review> captor = ArgumentCaptor.forClass(Review.class);
            verify(reviewRepository, times(1)).save(captor.capture());

            Review saved = captor.getValue();
            assertThat(saved.getRating()).isEqualByComparingTo("4.5");
            assertThat(saved.getComment()).isEqualTo("좋은 프로젝트였습니다. 감사합니다!");
            assertThat(saved.getToUser().getUserId()).isEqualTo(toUserId);

            verify(reviewImageRepository, times(1)).save(any(ReviewImage.class));
        }

        @Test
        @DisplayName("평점 범위 벗어남 - 예외 발생")
        void registerReviewInvalidRating() {
            ReviewRequestDto dto = new ReviewRequestDto(
                    contractId,
                    toUserId,
                    BigDecimal.valueOf(6),
                    "평점이 너무 높음",
                    null
            );
            
            assertThatThrownBy(() -> reviewService.registerReview(fromUserId, dto))
                    .isInstanceOf(ApplicationException.class)
                    .hasMessageContaining(ReviewErrorCase.INVALID_RATING.getMessage());

            verify(reviewRepository, never()).save(any());
        }

        @Test
        @DisplayName("존재하지 않는 계약 ID")
        void registerReviewInvalidContract() {
            ReviewRequestDto dto = new ReviewRequestDto(
                    999L,
                    toUserId,
                    BigDecimal.valueOf(4),
                    "계약 없음",
                    null
            );

            given(contractRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> reviewService.registerReview(fromUserId, dto))
                    .isInstanceOf(ApplicationException.class);

            verify(reviewRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("리뷰 조회 테스트")
    class GetReview {

        @Test
        @DisplayName("유저 리뷰 리스트 조회 성공")
        void getReviewListSuccess() {
            Long targetUserId = 2L;
            Review review = Review.builder()
                    .reviewId(1L)
                    .fromUser(User.builder().userId(1L).nickname("작성자").build())
                    .toUser(User.builder().userId(targetUserId).build())
                    .comment("굿굿")
                    .rating(BigDecimal.valueOf(5))
                    .reviewImageList(List.of(
                            ReviewImage.of(null, "https://cdn.s3/image1.png"),
                            ReviewImage.of(null, "https://cdn.s3/image2.png")
                    ))
                    .build();

            given(reviewRepository.findAllByToUser_UserId(targetUserId)).willReturn(List.of(review));

            var result = reviewService.getReviewsByUserId(targetUserId);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).comment()).contains("굿굿");
            assertThat(result.get(0).images()).containsExactly(
                    "https://cdn.s3/image1.png", "https://cdn.s3/image2.png"
            );
        }

        @Test
        @DisplayName("유저 리뷰 없음 = 빈 리스트 반환")
        void getEmptyReviewList() {
            Long userId = 3L;
            given(reviewRepository.findAllByToUser_UserId(userId)).willReturn(List.of());

            var result = reviewService.getReviewsByUserId(userId);

            assertThat(result).isEmpty();
        }
    }
}
