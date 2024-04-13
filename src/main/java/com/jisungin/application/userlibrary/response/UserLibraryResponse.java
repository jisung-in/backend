package com.jisungin.application.userlibrary.response;


import com.jisungin.domain.userlibrary.UserLibrary;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserLibraryResponse {

    private Long id;
    private String status;
    private Boolean hasReadingStatus;

    @Builder
    private UserLibraryResponse(Long id, String status, Boolean hasReadingStatus) {
        this.id = id;
        this.status = status;
        this.hasReadingStatus = hasReadingStatus;
    }

    public static UserLibraryResponse of(UserLibrary userLibrary) {
        if (userLibrary == null) {
            return empty();
        }

        return UserLibraryResponse.builder()
                .id(userLibrary.getId())
                .status(userLibrary.getStatus().getText())
                .hasReadingStatus(true)
                .build();
    }

    public static UserLibraryResponse empty() {
        return UserLibraryResponse.builder()
                .hasReadingStatus(false)
                .build();
    }

}
