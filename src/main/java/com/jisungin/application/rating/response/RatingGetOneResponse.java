package com.jisungin.application.rating.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RatingGetOneResponse {

    private Long id;

    private Double rating;

    private String isbn;

    @Builder
    public RatingGetOneResponse(Long id, Double rating, String isbn) {
        this.id = id;
        this.rating = rating;
        this.isbn = isbn;
    }

    public static RatingGetOneResponse of(Long id, Double rating, String isbn) {
        return RatingGetOneResponse.builder()
                .id(id)
                .rating(rating)
                .isbn(isbn)
                .build();
    }

}
