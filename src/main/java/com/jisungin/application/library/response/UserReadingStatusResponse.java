package com.jisungin.application.library.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserReadingStatusResponse {

    private String isbn;

    private String bookImage;

    private String bookTitle;

    private Double ratingAvg;

    @QueryProjection
    @Builder
    public UserReadingStatusResponse(String isbn, String bookImage, String bookTitle, Double ratingAvg) {
        this.isbn = isbn;
        this.bookImage = bookImage;
        this.bookTitle = bookTitle;
        this.ratingAvg = ratingAvg;
    }

}
