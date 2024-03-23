package com.jisungin.application.book.response;

import com.jisungin.domain.book.Book;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class BookResponse {

    private String title;
    private String content;
    private String isbn;
    private String publisher;
    private String imageUrl;
    private String thumbnail;
    private String[] authors;
    private Double ratingAverage;
    private LocalDateTime dateTime;

    @Builder
    private BookResponse(String title, String content, String isbn, String publisher, String thumbnail, String imageUrl,
                         String authors, Double ratingAverage, LocalDateTime dateTime) {
        this.title = title;
        this.content = content;
        this.isbn = isbn;
        this.publisher = publisher;
        this.imageUrl = imageUrl;
        this.thumbnail = thumbnail;
        this.authors = convertAuthorsToString(authors);
        this.ratingAverage = parseRatingAverage(ratingAverage);
        this.dateTime = dateTime;
    }

    public static BookResponse of(Book book) {
        return BookResponse.builder()
                .title(book.getTitle())
                .content(book.getContent())
                .authors(book.getAuthors())
                .isbn(book.getIsbn())
                .publisher(book.getPublisher())
                .dateTime(book.getDateTime())
                .thumbnail(book.getThumbnail())
                .imageUrl(book.getImageUrl())
                .ratingAverage(0.0)
                .build();
    }

    public static BookResponse of(Book book, Double ratingAverage) {
        return BookResponse.builder()
                .title(book.getTitle())
                .content(book.getContent())
                .authors(book.getAuthors())
                .isbn(book.getIsbn())
                .publisher(book.getPublisher())
                .dateTime(book.getDateTime())
                .thumbnail(book.getThumbnail())
                .imageUrl(book.getImageUrl())
                .ratingAverage(ratingAverage)
                .build();
    }

    private String[] convertAuthorsToString(String authors) {
        return authors.split(", ");
    }

    private Double parseRatingAverage(Double ratingAverage) {
        return ratingAverage == null ? 0.0 : BigDecimal.valueOf(ratingAverage)
                .setScale(1, RoundingMode.HALF_UP)
                .doubleValue();
    }

}
