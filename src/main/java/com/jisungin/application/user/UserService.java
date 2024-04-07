package com.jisungin.application.user;

import com.jisungin.application.PageResponse;
import com.jisungin.application.review.response.RatingFindAllResponse;
import com.jisungin.application.review.response.ReviewContentGetAllResponse;
import com.jisungin.application.review.response.ReviewContentResponse;
import com.jisungin.application.user.request.ReviewContentGetAllServiceRequest;
import com.jisungin.application.user.request.UserRatingGetAllServiceRequest;
import com.jisungin.domain.review.repository.ReviewRepository;
import com.jisungin.domain.reviewlike.repository.ReviewLikeRepository;
import com.jisungin.domain.user.User;
import com.jisungin.domain.user.repository.UserRepository;
import com.jisungin.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.jisungin.exception.ErrorCode.*;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {

    private final UserRepository userRepository;

    private final ReviewRepository reviewRepository;

    private final ReviewLikeRepository reviewLikeRepository;

    public PageResponse<RatingFindAllResponse> getUserRatings(Long userId, UserRatingGetAllServiceRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND));

        return reviewRepository.findAllRatingOrderBy(
                user.getId(), request.getOrderType(), request.getRating(), request.getSize(), request.getOffset());
    }

    public ReviewContentGetAllResponse getReviewContents(Long userId, ReviewContentGetAllServiceRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND));

        PageResponse<ReviewContentResponse> reviewContents = reviewRepository.findAllReviewContentOrderBy(
                user.getId(), request.getOrderType(), request.getSize(), request.getOffset());

        List<Long> reviewIds = reviewContents.getQueryResponse().stream()
                .map(ReviewContentResponse::getReviewId)
                .toList();

        List<Long> likeReviewIds = reviewLikeRepository.findLikeReviewByReviewId(userId, reviewIds);

        return ReviewContentGetAllResponse.of(reviewContents, likeReviewIds);
    }

}
