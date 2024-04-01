package com.jisungin.domain.review;

import com.jisungin.domain.BaseEntity;
import com.jisungin.domain.book.Book;
import com.jisungin.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_isbn")
    private Book book;

    @Column(name = "review_content", length = 1000)
    private String content;

    @Column(name = "review_rating")
    private Double rating;

    @Builder
    private Review(User user, Book book, String content, Double rating) {
        this.user = user;
        this.book = book;
        this.content = content;
        this.rating = rating;
    }

    public static Review create(User user, Book book, String content, Double rating) {
        return Review.builder()
                .user(user)
                .book(book)
                .content(content)
                .rating(rating)
                .build();
    }

}
