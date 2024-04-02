package com.jisungin.api.reviewlike;

import com.jisungin.api.ApiResponse;
import com.jisungin.api.oauth.Auth;
import com.jisungin.api.oauth.AuthContext;
import com.jisungin.application.reviewlike.ReviewLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
        reviewLikeService.likeReview(authContext.getUserId(), reviewId);
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/{reviewId}/likes")
    public ApiResponse<Void> unlikeReview(
            @PathVariable Long reviewId,
            @Auth AuthContext authContext
    ) {
        reviewLikeService.unlikeReview(authContext.getUserId(), reviewId);
        return ApiResponse.ok(null);
    }

}
