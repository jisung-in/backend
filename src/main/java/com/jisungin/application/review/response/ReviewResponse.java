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

    @Builder
    private ReviewResponse(Book book, String content) {
        this.book = book;
        this.content = content;
    }

    public static ReviewResponse of(Book book, String content) {
        return ReviewResponse.builder()
                .book(book)
                .content(content)
                .build();
    }

}
