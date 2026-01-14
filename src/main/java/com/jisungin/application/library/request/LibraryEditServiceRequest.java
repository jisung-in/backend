package com.jisungin.application.library.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LibraryEditServiceRequest {

    private String isbn;
    private String readingStatus;

    @Builder
    private LibraryEditServiceRequest(String isbn, String readingStatus) {
        this.isbn = isbn;
        this.readingStatus = readingStatus;
    }

}
