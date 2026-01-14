package com.jisungin.application.book.response;

import com.jisungin.infra.crawler.CrawledBook;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BookWithRankingResponse {

    private Long ranking;
    private String isbn;
    private String title;
    private String publisher;
    private String thumbnail;
    private String[] authors;
    private LocalDateTime dateTime;

    @Builder
    private BookWithRankingResponse(Long ranking, String isbn, String title, String publisher, String thumbnail,
                                    String[] authors, LocalDateTime dateTime) {
        this.ranking = ranking;
        this.isbn = isbn;
        this.title = title;
        this.publisher = publisher;
        this.thumbnail = thumbnail;
        this.authors = authors;
        this.dateTime = dateTime;
    }

    public static BookWithRankingResponse of(Long ranking, String isbn, String title, String publisher, String thumbnail,
                                             String[] authors, LocalDateTime dateTime) {
        return BookWithRankingResponse.builder()
                .ranking(ranking)
                .isbn(isbn)
                .title(title)
                .publisher(publisher)
                .thumbnail(thumbnail)
                .authors(authors)
                .dateTime(dateTime)
                .build();
    }

    public static BookWithRankingResponse ofRankIncrement(Long ranking, CrawledBook crawledBook) {
        return BookWithRankingResponse.builder()
                .ranking(ranking + 1)
                .isbn(crawledBook.getIsbn())
                .title(crawledBook.getTitle())
                .publisher(crawledBook.getPublisher())
                .thumbnail(crawledBook.getThumbnail())
                .authors(crawledBook.getAuthors())
                .dateTime(crawledBook.getDateTime())
                .build();
    }

}
