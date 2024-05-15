package com.jisungin.api.review;

import com.jisungin.api.ApiResponse;
import com.jisungin.api.support.Auth;
import com.jisungin.api.review.request.ReviewCreateRequest;
import com.jisungin.application.review.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/v1/reviews")
@RequiredArgsConstructor
@RestController
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ApiResponse<Void> createReview(@Valid @RequestBody ReviewCreateRequest request,
                                          @Auth Long userId) {
        reviewService.createReview(request.toServiceRequest(), userId);
        return ApiResponse.ok();
    }

    @DeleteMapping("/{reviewId}")
    public ApiResponse<Void> deleteReview(@PathVariable Long reviewId, @Auth Long userId) {
        reviewService.deleteReview(reviewId, userId);
        return ApiResponse.ok();
    }

}
