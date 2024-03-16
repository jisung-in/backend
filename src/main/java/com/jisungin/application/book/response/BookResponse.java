package com.jisungin.application.book.response;

import com.jisungin.domain.book.Book;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class BookResponse {

    private Long id;
    private String title;
    private String content;
    private String isbn;
    private String publisher;
    private String url;
    private String thumbnail;
    private String[] authors;
    private LocalDateTime dateTime;

    @Builder
    private BookResponse(Long id, String title, String content, String isbn, String publisher, String url,
                         String thumbnail, String authors, LocalDateTime dateTime) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.isbn = isbn;
        this.publisher = publisher;
        this.url = url;
        this.thumbnail = thumbnail;
        this.authors = convertToString(authors);
        this.dateTime = dateTime;
    }

    public static BookResponse of(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .content(book.getContent())
                .authors(book.getAuthors())
                .isbn(book.getIsbn())
                .publisher(book.getPublisher())
                .dateTime(book.getDateTime())
                .url(book.getUrl())
                .thumbnail(book.getThumbnail())
                .build();
    }

    private String[] convertToString(String authors) {
        return authors.split(", ");
    }

}
