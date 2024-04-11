package com.jisungin.api.userlibrary.request;

import com.jisungin.application.userlibrary.request.UserLibraryCreateServiceRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserLibraryCreateRequest {

    @NotBlank(message = "책 isbn 입력은 필수 입니다.")
    private String isbn;

    @NotBlank(message = "독서 상태 정보 입력은 필수 입니다.")
    private String readingStatus;

    @Builder
    private UserLibraryCreateRequest(String isbn, String readingStatus) {
        this.isbn = isbn;
        this.readingStatus = readingStatus;
    }

    public UserLibraryCreateServiceRequest toServiceRequest() {
        return UserLibraryCreateServiceRequest.builder()
                .isbn(isbn)
                .readingStatus(readingStatus)
                .build();
    }

}
