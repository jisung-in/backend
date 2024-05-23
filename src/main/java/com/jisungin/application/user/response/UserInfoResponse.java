package com.jisungin.application.user.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserInfoResponse {

    private String userImage;

    private String userName;

    @Builder
    public UserInfoResponse(String userImage, String userName) {
        this.userImage = userImage;
        this.userName = userName;
    }

    public static UserInfoResponse of(String userName, String userImage) {
        return UserInfoResponse.builder()
                .userImage(userImage)
                .userName(userName)
                .build();
    }

}
