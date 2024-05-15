package com.jisungin.api.rating;

import com.jisungin.api.ApiResponse;
import com.jisungin.api.rating.request.RatingCreateRequest;
import com.jisungin.api.rating.request.RatingUpdateRequest;
import com.jisungin.api.support.Auth;
import com.jisungin.application.rating.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/v1/ratings")
@RequiredArgsConstructor
@RestController
public class RatingController {

    private final RatingService ratingService;

    @PostMapping
    public ApiResponse<Void> createRating(@Auth Long userId, @Valid RatingCreateRequest request) {
        ratingService.creatingRating(userId, request.toServiceRequest());
        return ApiResponse.ok();
    }

    @PatchMapping("/{ratingId}")
    public ApiResponse<Void> updateRating(
            @Auth Long userId, @PathVariable Long ratingId, @Valid RatingUpdateRequest request) {
        ratingService.updateRating(userId, ratingId, request.toServiceRequest());
        return ApiResponse.ok();
    }

    @DeleteMapping("/{ratingId}")
    public ApiResponse<Void> deleteRating(@Auth Long userId, @PathVariable Long ratingId) {
        ratingService.deleteRating(userId, ratingId);
        return ApiResponse.ok();
    }

}
