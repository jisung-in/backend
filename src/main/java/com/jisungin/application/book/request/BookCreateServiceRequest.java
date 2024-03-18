package com.jisungin.application.book.request;

import com.jisungin.domain.book.Book;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BookCreateServiceRequest {

    private String title;
    private String contents;
    private String url;
    private String isbn;
    private String authors;
    private String publisher;
    private String thumbnail;
    private LocalDateTime dateTime;

    @Builder
    private BookCreateServiceRequest(String title, String contents, String url, String isbn, String authors,
                                     String publisher, String thumbnail, LocalDateTime dateTime) {
        this.title = title;
        this.contents = contents;
        this.url = url;
        this.isbn = isbn;
        this.authors = authors;
        this.publisher = publisher;
        this.thumbnail = thumbnail;
        this.dateTime = dateTime;
    }

    public Book toEntity() {
        return Book.builder()
                .title(title)
                .content(contents)
                .url(url)
                .isbn(isbn)
                .dateTime(dateTime)
                .authors(authors)
                .publisher(publisher)
                .thumbnail(thumbnail)
                .build();
    }

}
