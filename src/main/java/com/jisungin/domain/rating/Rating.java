package com.jisungin.domain.rating;

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
public class Rating extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rating_id")
    private Long id;

    private Double rating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_isbn")
    private Book book;

    @Builder
    public Rating(Double rating, User user, Book book) {
        this.rating = rating;
        this.user = user;
        this.book = book;
    }

    public static Rating create(Double rating, User user, Book book) {
        return Rating.builder()
                .rating(rating)
                .user(user)
                .book(book)
                .build();
    }

    public void updateRating(Double rating) {
        this.rating = rating;
    }

}
