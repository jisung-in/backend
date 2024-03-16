package com.jisungin.domain.book;

import com.jisungin.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Book extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Long id;

    @Column(name = "book_title")
    private String title;

    @Column(name = "book_content")
    private String content;

    @Column(name = "book_authors")
    private String authors;

    @Column(name = "book_isbn")
    private String isbn;

    @Column(name = "book_publisher")
    private String publisher;

    @Column(name = "book_url")
    private String url;

    @Column(name = "book_thumbnail")
    private String thumbnail;

    @Column(name = "book_date_time")
    private LocalDateTime dateTime;


    @Builder
    private Book(String title, String content, String authors, String isbn, String publisher, String url,
                 String thumbnail, LocalDateTime dateTime) {
        this.title = title;
        this.content = content;
        this.authors = authors;
        this.isbn = isbn;
        this.publisher = publisher;
        this.url = url;
        this.thumbnail = thumbnail;
        this.dateTime = dateTime;
    }

}
