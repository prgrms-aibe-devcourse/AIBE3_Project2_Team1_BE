package com.hotsix.server.review.service;

import com.hotsix.server.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    // 리뷰 생성, 조회, 평판 집계 등 로직 구현
}
