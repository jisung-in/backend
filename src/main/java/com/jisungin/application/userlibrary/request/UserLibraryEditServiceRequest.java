package com.jisungin.application.userlibrary.request;

import com.jisungin.api.userlibrary.request.UserLibraryCreateRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserLibraryEditServiceRequest {

    private String isbn;
    private String readingStatus;

    @Builder
    private UserLibraryEditServiceRequest(String isbn, String readingStatus) {
        this.isbn = isbn;
        this.readingStatus = readingStatus;
    }

}
