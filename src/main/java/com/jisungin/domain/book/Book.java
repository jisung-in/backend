package com.jisungin.domain.book;

import com.jisungin.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
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
    @Column(name = "book_isbn")
    private String isbn;

    @Column(name = "book_title")
    private String title;

    @Lob
    @Column(name = "book_content", length = 3000)
    private String content;

    @Column(name = "book_authors")
    private String authors;

    @Column(name = "book_publisher")
    private String publisher;

    @Column(name = "book_image")
    private String imageUrl;

    @Column(name = "book_thumbnail")
    private String thumbnail;

    @Column(name = "book_date_time")
    private LocalDateTime dateTime;


    @Builder
    private Book(String isbn, String title, String content, String authors, String publisher, String imageUrl,
                 String thumbnail, LocalDateTime dateTime) {
        this.isbn = isbn;
        this.title = title;
        this.content = content;
        this.authors = authors;
        this.publisher = publisher;
        this.imageUrl = imageUrl;
        this.thumbnail = thumbnail;
        this.dateTime = dateTime;
    }

}
