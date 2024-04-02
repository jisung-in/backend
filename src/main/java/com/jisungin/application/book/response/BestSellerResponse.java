package com.jisungin.application.book.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BestSellerResponse {

    private Long ranking;
    private String isbn;
    private String title;
    private String publisher;
    private String thumbnail;
    private String[] authors;
    private LocalDateTime dateTime;

    @Builder
    private BestSellerResponse(Long ranking, String isbn, String title, String publisher, String thumbnail,
                               String[] authors, LocalDateTime dateTime) {
        this.ranking = ranking;
        this.isbn = isbn;
        this.title = title;
        this.publisher = publisher;
        this.thumbnail = thumbnail;
        this.authors = authors;
        this.dateTime = dateTime;
    }

    public static BestSellerResponse of(Long ranking, String isbn, String title, String publisher, String thumbnail,
                                        String[] authors, LocalDateTime dateTime) {
        return BestSellerResponse.builder()
                .ranking(ranking)
                .isbn(isbn)
                .title(title)
                .publisher(publisher)
                .thumbnail(thumbnail)
                .authors(authors)
                .dateTime(dateTime)
                .build();
    }

}
