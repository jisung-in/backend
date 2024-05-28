package com.jisungin.application.reviewlike;

import com.jisungin.application.reviewlike.response.ReviewIds;
import com.jisungin.domain.review.Review;
import com.jisungin.domain.review.repository.ReviewRepository;
import com.jisungin.domain.reviewlike.ReviewLike;
import com.jisungin.domain.reviewlike.repository.ReviewLikeRepository;
import com.jisungin.domain.user.User;
import com.jisungin.domain.user.repository.UserRepository;
import com.jisungin.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.jisungin.exception.ErrorCode.*;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ReviewLikeService {

    private final ReviewLikeRepository reviewLikeRepository;

    private final UserRepository userRepository;

    private final ReviewRepository reviewRepository;

    public ReviewIds findLikeReviewIds(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND));

        return ReviewIds.of(reviewLikeRepository.findReviewIdsByUserId(user.getId()));
    }

    @Transactional
    public void likeReview(Long userId, Long reviewId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(REVIEW_NOT_FOUND));

        // 좋아요가 이미 존재할 경우, 400 에러
        if (reviewLikeRepository.findByUserAndReview(user, review).isPresent()) {
            throw new BusinessException(LIKE_EXIST);
        }

        // 없는 경우, 리뷰 좋아요 저장
        ReviewLike reviewLike = ReviewLike.likeReview(user, review);
        reviewLikeRepository.save(reviewLike);
    }

    @Transactional
    public void unlikeReview(Long userId, Long reviewId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(REVIEW_NOT_FOUND));

        ReviewLike reviewLike = reviewLikeRepository.findByUserAndReview(user, review)
                .orElseThrow(() -> new BusinessException(REVIEW_LIKE_NOT_FOUND));

        reviewLikeRepository.delete(reviewLike);
    }

}
