package com.jisungin.application.rating.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RatingCreateServiceRequest {

    private String bookIsbn;

    private Double rating;

    @Builder
    public RatingCreateServiceRequest(String bookIsbn, Double rating) {
        this.bookIsbn = bookIsbn;
        this.rating = rating;
    }

}
