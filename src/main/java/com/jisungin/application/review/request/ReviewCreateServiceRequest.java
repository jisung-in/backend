package com.jisungin.application.review.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewCreateServiceRequest {

    private String bookIsbn;

    private String content;

    private Double rating;

    @Builder
    public ReviewCreateServiceRequest(String bookIsbn, String content, Double rating) {
        this.bookIsbn = bookIsbn;
        this.content = content;
        this.rating = rating;
    }

}
