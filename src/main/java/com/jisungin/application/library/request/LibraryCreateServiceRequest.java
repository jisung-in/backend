package com.jisungin.application.library.request;

import com.jisungin.domain.ReadingStatus;
import com.jisungin.domain.book.Book;
import com.jisungin.domain.library.Library;
import com.jisungin.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LibraryCreateServiceRequest {

    private String isbn;
    private String readingStatus;

    @Builder
    private LibraryCreateServiceRequest(String isbn, String readingStatus) {
        this.isbn = isbn;
        this.readingStatus = readingStatus;
    }

    public Library toEntity(User user, Book book) {
        return Library.builder()
                .user(user)
                .book(book)
                .status(ReadingStatus.createReadingStatus(readingStatus))
                .build();
    }

}
