package com.jisungin.api.library.request;

import com.jisungin.application.library.request.LibraryEditServiceRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LibraryEditRequest {

    @NotBlank(message = "책 isbn 입력은 필수 입니다.")
    private String isbn;

    @NotBlank(message = "독서 상태 정보 입력은 필수 입니다.")
    private String readingStatus;

    @Builder
    private LibraryEditRequest(String isbn, String readingStatus) {
        this.isbn = isbn;
        this.readingStatus = readingStatus;
    }

    public LibraryEditServiceRequest toServiceRequest() {
        return LibraryEditServiceRequest.builder()
                .isbn(isbn)
                .readingStatus(readingStatus)
                .build();
    }

}
