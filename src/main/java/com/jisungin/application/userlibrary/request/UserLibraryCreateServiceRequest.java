package com.jisungin.application.userlibrary.request;

import com.jisungin.domain.ReadingStatus;
import com.jisungin.domain.book.Book;
import com.jisungin.domain.mylibrary.UserLibrary;
import com.jisungin.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserLibraryCreateServiceRequest {

    private String isbn;
    private String readingStatus;

    @Builder
    private UserLibraryCreateServiceRequest(String isbn, String readingStatus) {
        this.isbn = isbn;
        this.readingStatus = readingStatus;
    }

    public UserLibrary toEntity(User user, Book book) {
        return UserLibrary.builder()
                .user(user)
                .book(book)
                .status(ReadingStatus.createReadingStatus(readingStatus))
                .build();
    }

}
