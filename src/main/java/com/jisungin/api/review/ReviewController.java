package com.jisungin.api.review;

import com.jisungin.api.ApiResponse;
import com.jisungin.api.oauth.Auth;
import com.jisungin.api.oauth.AuthContext;
import com.jisungin.api.review.request.ReviewCreateRequest;
import com.jisungin.application.review.ReviewService;
import com.jisungin.application.review.response.ReviewResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/v1/reviews")
@RequiredArgsConstructor
@RestController
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ApiResponse<ReviewResponse> createReview(@Valid @RequestBody ReviewCreateRequest request,
                                                    @Auth AuthContext authContext) {
        return ApiResponse.ok(reviewService.createReview(request.toServiceRequest(), authContext.getUserId()));
    }

    @DeleteMapping("/{reviewId}")
    public ApiResponse<Void> deleteReview(@PathVariable Long reviewId,
                                          @Auth AuthContext authContext) {
        reviewService.deleteReview(reviewId, authContext.getUserId());
        return ApiResponse.ok(null);
    }

}
