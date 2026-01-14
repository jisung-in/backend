package com.jisungin.api.reviewlike;

import com.jisungin.api.ApiResponse;
import com.jisungin.api.support.Auth;
import com.jisungin.application.reviewlike.response.ReviewIds;
import com.jisungin.application.reviewlike.ReviewLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/v1/reviews")
@RequiredArgsConstructor
@RestController
public class ReviewLikeController {

    private final ReviewLikeService reviewLikeService;

    @GetMapping("/likes")
    public ApiResponse<ReviewIds> findLikeReviewIds(@Auth Long userId) {
        return ApiResponse.ok(reviewLikeService.findLikeReviewIds(userId));
    }

    @PostMapping("/{reviewId}/likes")
    public ApiResponse<Void> likeReview(
            @PathVariable Long reviewId,
            @Auth Long userId
    ) {
        reviewLikeService.likeReview(userId, reviewId);
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/{reviewId}/likes")
    public ApiResponse<Void> unlikeReview(
            @PathVariable Long reviewId,
            @Auth Long userId
    ) {
        reviewLikeService.unlikeReview(userId, reviewId);
        return ApiResponse.ok(null);
    }

}
