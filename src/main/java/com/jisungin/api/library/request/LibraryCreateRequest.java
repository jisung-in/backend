package com.jisungin.api.library.request;

import com.jisungin.application.library.request.LibraryCreateServiceRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LibraryCreateRequest {

    @NotBlank(message = "책 isbn 입력은 필수 입니다.")
    private String isbn;

    @NotBlank(message = "독서 상태 정보 입력은 필수 입니다.")
    private String readingStatus;

    @Builder
    private LibraryCreateRequest(String isbn, String readingStatus) {
        this.isbn = isbn;
        this.readingStatus = readingStatus;
    }

    public LibraryCreateServiceRequest toServiceRequest() {
        return LibraryCreateServiceRequest.builder()
                .isbn(isbn)
                .readingStatus(readingStatus)
                .build();
    }

}
