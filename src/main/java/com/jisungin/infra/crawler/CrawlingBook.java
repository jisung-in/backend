package com.jisungin.infra.crawler;

import com.jisungin.application.book.request.BookCreateServiceRequest;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CrawlingBook {

    private String title;
    private String content;
    private String isbn;
    private String publisher;
    private String imageUrl;
    private String thumbnail;
    private String[] authors;
    private LocalDateTime dateTime;

    @Builder
    private CrawlingBook(String title, String content, String isbn, String publisher, String imageUrl, String thumbnail,
                         String authors, LocalDateTime dateTime) {
        this.title = title;
        this.content = content;
        this.isbn = isbn;
        this.publisher = publisher;
        this.imageUrl = imageUrl;
        this.thumbnail = thumbnail;
        this.authors = convertAuthorsToArr(authors);
        this.dateTime = dateTime;
    }

    public static CrawlingBook of(String title, String content, String isbn, String publisher, String imageUrl,
                                  String thumbnail, String authors, LocalDateTime dateTime) {
        return CrawlingBook.builder()
                .title(title)
                .content(content)
                .isbn(isbn)
                .publisher(publisher)
                .imageUrl(imageUrl)
                .thumbnail(thumbnail)
                .authors(authors)
                .dateTime(dateTime)
                .build();
    }

    public BookCreateServiceRequest toServiceRequest() {
        return BookCreateServiceRequest.builder()
                .title(title)
                .contents(content)
                .isbn(isbn)
                .publisher(publisher)
                .imageUrl(imageUrl)
                .thumbnail(thumbnail)
                .authors(convertAuthorsToString())
                .dateTime(dateTime)
                .build();
    }

    private String[] convertAuthorsToArr(String authors) {
        return authors.split(" 저| 공저| 글| 편저| 원저| 기획|&")[0].split(",");
    }

    private String convertAuthorsToString() {
        return String.join(",", authors);
    }

}
