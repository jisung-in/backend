package com.jisungin.application.rating.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RatingCreateResponse {

    private Long id;

    private Double rating;

    private String isbn;

    @Builder
    public RatingCreateResponse(Long id, Double rating, String isbn) {
        this.id = id;
        this.rating = rating;
        this.isbn = isbn;
    }

    public static RatingCreateResponse of(Long id, Double rating, String isbn) {
        return RatingCreateResponse.builder()
                .id(id)
                .rating(rating)
                .isbn(isbn)
                .build();
    }

}
