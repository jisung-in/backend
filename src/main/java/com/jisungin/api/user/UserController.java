package com.jisungin.api.user;

import com.jisungin.api.ApiResponse;
import com.jisungin.api.oauth.Auth;
import com.jisungin.api.user.request.ReviewContentGetAllRequest;
import com.jisungin.api.user.request.UserRatingGetAllRequest;
import com.jisungin.api.user.request.UserReadingStatusGetAllRequest;
import com.jisungin.application.PageResponse;
import com.jisungin.application.review.response.RatingFindAllResponse;
import com.jisungin.application.review.response.ReviewContentGetAllResponse;
import com.jisungin.application.user.UserService;
import com.jisungin.application.userlibrary.response.UserReadingStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/v1/users")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    @GetMapping("/ratings")
    public ApiResponse<PageResponse<RatingFindAllResponse>> getUserRatings(
            @ModelAttribute UserRatingGetAllRequest request,
            @Auth Long userId
    ) {
        PageResponse<RatingFindAllResponse> response = userService.getUserRatings(
                userId, request.toService());

        return ApiResponse.ok(response);
    }

    @GetMapping("/reviews")
    public ApiResponse<ReviewContentGetAllResponse> getReviewContents(
            @ModelAttribute ReviewContentGetAllRequest request,
            @Auth Long userId
    ) {
        ReviewContentGetAllResponse response = userService.getReviewContents(
                userId, request.toService());

        return ApiResponse.ok(response);
    }

    @GetMapping("/statuses")
    public ApiResponse<PageResponse<UserReadingStatusResponse>> getReadingStatuses(
            @ModelAttribute UserReadingStatusGetAllRequest request,
            @Auth Long userId
    ) {
        PageResponse<UserReadingStatusResponse> response = userService
                .getUserReadingStatuses(userId, request.toService());

        return ApiResponse.ok(response);
    }

}
