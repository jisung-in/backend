package com.jisungin.application.review.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RatingFindAllResponse {

    private String isbn;

    private String title;

    private String image;

    private Double rating;

    @Builder
    @QueryProjection
    public RatingFindAllResponse(String isbn, String title, String image, Double rating) {
        this.isbn = isbn;
        this.title = title;
        this.image = image;
        this.rating = rating;
    }

}
