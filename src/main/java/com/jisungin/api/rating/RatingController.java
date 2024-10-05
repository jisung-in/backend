package com.jisungin.api.rating;

import com.jisungin.api.ApiResponse;
import com.jisungin.api.rating.request.RatingCreateRequest;
import com.jisungin.api.rating.request.RatingUpdateRequest;
import com.jisungin.api.rating.request.UserRatingGetAllRequest;
import com.jisungin.api.support.Auth;
import com.jisungin.api.support.GuestOrAuth;
import com.jisungin.application.PageResponse;
import com.jisungin.application.rating.RatingService;
import com.jisungin.application.rating.response.RatingCreateResponse;
import com.jisungin.application.rating.response.RatingGetOneResponse;
import com.jisungin.application.rating.response.RatingGetResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/v1")
@RequiredArgsConstructor
@RestController
public class RatingController {

    private final RatingService ratingService;

    @PostMapping("/ratings")
    public ApiResponse<RatingCreateResponse> createRating(@Auth Long userId, @Valid @RequestBody RatingCreateRequest request) {
        return ApiResponse.ok(ratingService.creatingRating(userId, request.toServiceRequest()));
    }

    @GetMapping("/ratings")
    public ApiResponse<RatingGetOneResponse> getRating(@GuestOrAuth Long userId, @RequestParam String isbn) {
        return ApiResponse.ok(ratingService.getRating(userId, isbn));
    }

    @PatchMapping("/ratings/{ratingId}")
    public ApiResponse<Void> updateRating(
            @Auth Long userId, @PathVariable Long ratingId, @Valid @RequestBody RatingUpdateRequest request) {
        ratingService.updateRating(userId, ratingId, request.toServiceRequest());
        return ApiResponse.ok();
    }

    @DeleteMapping("/ratings/{ratingId}")
    public ApiResponse<Void> deleteRating(@Auth Long userId, @PathVariable Long ratingId) {
        ratingService.deleteRating(userId, ratingId);
        return ApiResponse.ok();
    }

    @GetMapping("/users/ratings")
    public ApiResponse<PageResponse<RatingGetResponse>> getUserRatings(
            @ModelAttribute UserRatingGetAllRequest request,
            @Auth Long userId
    ) {
        PageResponse<RatingGetResponse> response = ratingService.getUserRatings(userId, request.toService());

        return ApiResponse.ok(response);
    }
}
