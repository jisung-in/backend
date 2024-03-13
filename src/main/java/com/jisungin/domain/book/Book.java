package com.jisungin.domain.book;

import com.jisungin.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Arrays;

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

    @Column(name = "book_date_time")
    private LocalDateTime dateTime;

    @Column(name = "book_url")
    private String url;

    @Builder
    private Book(String title, String content, String[] authors, String isbn, String publisher, LocalDateTime dateTime, String url) {
        this.title = title;
        this.content = content;
        this.authors = Arrays.toString(authors);
        this.isbn = isbn;
        this.publisher = publisher;
        this.dateTime = dateTime;
        this.url = url;
    }

}
