package com.jisungin.application.review.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewWithRatingResponse {

    private Long reviewId;
    private Long ratingId;
    private String username;
    private String profileImage;
    private String reviewContent;
    private Double starRating;
    private Long likeCount;

    @Builder
    @QueryProjection
    public ReviewWithRatingResponse(Long reviewId, Long ratingId, String username, String profileImage,
                                     String reviewContent, Double starRating, Long likeCount) {
        this.reviewId = reviewId;
        this.ratingId = ratingId;
        this.username = username;
        this.profileImage = profileImage;
        this.reviewContent = reviewContent;
        this.starRating = starRating;
        this.likeCount = likeCount;
    }

    public static ReviewWithRatingResponse of(Long reviewId, Long ratingId, String username, String profileImage,
                                              String reviewContent, Double starRating, Long likeCount) {
       return ReviewWithRatingResponse.builder()
               .reviewId(reviewId)
               .ratingId(ratingId)
               .username(username)
               .profileImage(profileImage)
               .reviewContent(reviewContent)
               .starRating(starRating)
               .likeCount(likeCount)
               .build();
    }

}
