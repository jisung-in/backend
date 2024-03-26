package com.jisungin.application.review.response;

import com.jisungin.domain.book.Book;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewResponse {

    private Book book;

    private String content;

    private Double rating;

    @Builder
    private ReviewResponse(Book book, String content, Double rating) {
        this.book = book;
        this.content = content;
        this.rating = rating;
    }

    public static ReviewResponse of(Book book, String content, Double rating) {
        return ReviewResponse.builder()
                .book(book)
                .content(content)
                .rating(rating)
                .build();
    }

}
