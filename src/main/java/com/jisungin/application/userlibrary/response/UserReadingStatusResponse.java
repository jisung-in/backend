package com.jisungin.application.userlibrary.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserReadingStatusResponse {

    private String bookImage;

    private String bookTitle;

    private Double ratingAvg;

    @QueryProjection
    @Builder
    public UserReadingStatusResponse(String bookImage, String bookTitle, Double ratingAvg) {
        this.bookImage = bookImage;
        this.bookTitle = bookTitle;
        this.ratingAvg = ratingAvg;
    }

}
