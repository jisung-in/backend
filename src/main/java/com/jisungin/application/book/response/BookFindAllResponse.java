package com.jisungin.application.book.response;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BookFindAllResponse {

    private String isbn;
    private String title;
    private String publisher;
    private String thumbnail;
    private String[] authors;
    private LocalDateTime dateTime;

    @Builder
    @QueryProjection
    public BookFindAllResponse(String isbn, String title, String publisher, String thumbnail, String authors,
                               LocalDateTime dateTime) {
        this.isbn = isbn;
        this.title = title;
        this.publisher = publisher;
        this.thumbnail = thumbnail;
        this.authors = convertAuthorsToArr(authors);
        this.dateTime = dateTime;
    }

    public static BookFindAllResponse of(String isbn, String title, String publisher, String thumbnail, String authors,
                                         LocalDateTime dateTime) {
        return BookFindAllResponse.builder()
                .isbn(isbn)
                .title(title)
                .publisher(publisher)
                .thumbnail(thumbnail)
                .authors(authors)
                .dateTime(dateTime)
                .build();
    }

    private String[] convertAuthorsToArr(String authors) {
        return authors.split(",");
    }

}
