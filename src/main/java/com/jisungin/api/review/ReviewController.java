package com.jisungin.api.review;

import com.jisungin.api.ApiResponse;
import com.jisungin.api.review.request.ReviewContentGetAllRequest;
import com.jisungin.api.review.request.ReviewCreateRequest;
import com.jisungin.api.support.Auth;
import com.jisungin.application.OffsetLimit;
import com.jisungin.application.SliceResponse;
import com.jisungin.application.review.ReviewService;
import com.jisungin.application.review.response.ReviewContentGetAllResponse;
import com.jisungin.application.review.response.ReviewWithRatingResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/v1")
@RequiredArgsConstructor
@RestController
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/books/{isbn}/reviews")
    public ApiResponse<SliceResponse<ReviewWithRatingResponse>> findBookReviews(
            @PathVariable String isbn,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "8") Integer size,
            @RequestParam(required = false, defaultValue = "like") String order
    ) {
        return ApiResponse.ok(reviewService.findBookReviews(isbn, OffsetLimit.of(page, size, order)));
    }

    @GetMapping("/books/{isbn}/reviews/count")
    public ApiResponse<Long> findBookReviewsCount(@PathVariable String isbn) {
        return ApiResponse.ok(reviewService.findBookReviewsCount(isbn));
    }

    @PostMapping("/reviews")
    public ApiResponse<Void> createReview(@Valid @RequestBody ReviewCreateRequest request,
                                          @Auth Long userId) {
        reviewService.createReview(request.toServiceRequest(), userId);
        return ApiResponse.ok();
    }

    @DeleteMapping("/reviews/{reviewId}")
    public ApiResponse<Void> deleteReview(@PathVariable Long reviewId, @Auth Long userId) {
        reviewService.deleteReview(reviewId, userId);
        return ApiResponse.ok();
    }

    @GetMapping("/users/reviews")
    public ApiResponse<ReviewContentGetAllResponse> getReviewContents(
            @ModelAttribute ReviewContentGetAllRequest request,
            @Auth Long userId
    ) {
        ReviewContentGetAllResponse response = reviewService.getReviewContents(userId, request.toService());

        return ApiResponse.ok(response);
    }
}
