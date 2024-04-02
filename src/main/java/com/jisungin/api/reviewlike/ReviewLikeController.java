package com.jisungin.api.reviewlike;

import com.jisungin.api.ApiResponse;
import com.jisungin.api.oauth.Auth;
import com.jisungin.api.oauth.AuthContext;
import com.jisungin.application.reviewlike.ReviewLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/v1/reviews")
@RequiredArgsConstructor
@RestController
public class ReviewLikeController {

    private final ReviewLikeService reviewLikeService;

    @PostMapping("/{reviewId}/likes")
    public ApiResponse<Void> likeReview(
            @PathVariable Long reviewId,
            @Auth AuthContext authContext
    ) {
        reviewLikeService.createReviewLike(authContext.getUserId(), reviewId);
        return ApiResponse.ok(null);
    }

}
