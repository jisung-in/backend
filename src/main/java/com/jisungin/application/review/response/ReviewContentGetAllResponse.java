package com.jisungin.application.review.response;

import com.jisungin.application.PageResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ReviewContentGetAllResponse {

    private PageResponse<ReviewContentResponse> reviewContents;

    private List<Long> userLikes;

    @Builder
    private ReviewContentGetAllResponse(PageResponse<ReviewContentResponse> reviewContents, List<Long> userLikes) {
        this.reviewContents = reviewContents;
        this.userLikes = userLikes;
    }

    public static ReviewContentGetAllResponse of(
            PageResponse<ReviewContentResponse> reviewContents, List<Long> userLikes) {
        return ReviewContentGetAllResponse.builder()
                .reviewContents(reviewContents)
                .userLikes(userLikes)
                .build();
    }

}
