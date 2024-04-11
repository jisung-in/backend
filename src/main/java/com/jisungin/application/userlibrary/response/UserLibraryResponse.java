package com.jisungin.application.userlibrary.response;


import com.jisungin.domain.mylibrary.UserLibrary;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserLibraryResponse {

    private Long id;
    private String status;

    @Builder
    private UserLibraryResponse(Long id, String status) {
        this.id = id;
        this.status = status;
    }

    public static UserLibraryResponse of(UserLibrary userLibrary) {
        return UserLibraryResponse.builder()
                .id(userLibrary.getId())
                .status(userLibrary.getStatus().getText())
                .build();

    }

}
