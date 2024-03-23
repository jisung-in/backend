package com.jisungin.application.book.request;

import com.jisungin.domain.book.Book;
import com.jisungin.infra.crawler.CrawlingBook;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BookCreateServiceRequest {

    private String title;
    private String contents;
    private String isbn;
    private String authors;
    private String publisher;
    private String imageUrl;
    private String thumbnail;
    private LocalDateTime dateTime;

    @Builder
    private BookCreateServiceRequest(String title, String contents, String isbn, String authors, String publisher,
                                     String imageUrl, String thumbnail, LocalDateTime dateTime) {
        this.title = title;
        this.contents = contents;
        this.isbn = isbn;
        this.authors = authors;
        this.publisher = publisher;
        this.imageUrl = imageUrl;
        this.thumbnail = thumbnail;
        this.dateTime = dateTime;
    }

    public Book toEntity() {
        return Book.builder()
                .title(title)
                .content(contents)
                .isbn(isbn)
                .dateTime(dateTime)
                .authors(authors)
                .publisher(publisher)
                .thumbnail(thumbnail)
                .imageUrl(imageUrl)
                .build();
    }

    public void addCrawlingData(CrawlingBook crawlingBook) {
        this.imageUrl = crawlingBook.getImageUrl();
        this.contents = crawlingBook.isBlankContent() ? this.contents : crawlingBook.getContent();
    }

}
