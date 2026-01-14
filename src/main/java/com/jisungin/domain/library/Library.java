package com.jisungin.domain.library;

import com.jisungin.domain.BaseEntity;
import com.jisungin.domain.ReadingStatus;
import com.jisungin.domain.book.Book;
import com.jisungin.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Library extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "library_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_isbn")
    private Book book;

    @Enumerated(EnumType.STRING)
    @Column(name = "library_reading_status")
    private ReadingStatus status;

    @Builder
    private Library(User user, Book book, ReadingStatus status) {
        this.user = user;
        this.book = book;
        this.status = status;
    }

    public boolean isLibraryOwner(Long userId) {
        return user.isMe(userId);
    }

    public boolean isSameBook(String isbn) {
        return book.isSame(isbn);
    }

    public void editReadingStatus(ReadingStatus status) {
        this.status = status;
    }

}
