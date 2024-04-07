package com.jisungin.application.review.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewContentResponse {

    private Long reviewId;

    private String userImage;

    private String userName;

    private Double rating;

    private String content;

    private String isbn;

    private String title;

    private String bookImage;

    @Builder
    @QueryProjection
    public ReviewContentResponse(
            Long reviewId, String userImage, String userName, Double rating, String content,
            String isbn, String title, String bookImage) {
        this.reviewId = reviewId;
        this.userImage = userImage;
        this.userName = userName;
        this.rating = rating;
        this.content = content;
        this.isbn = isbn;
        this.title = title;
        this.bookImage = bookImage;
    }

}
