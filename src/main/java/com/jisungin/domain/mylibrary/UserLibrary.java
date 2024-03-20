package com.jisungin.domain.mylibrary;

import com.jisungin.domain.BaseEntity;
import com.jisungin.domain.ReadingStatus;
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
public class UserLibrary extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_library_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_isbn")
    private Book book;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_library_reading_status")
    private ReadingStatus status;

    @Builder
    private UserLibrary(User user, Book book, ReadingStatus status) {
        this.user = user;
        this.book = book;
        this.status = status;
    }

}
