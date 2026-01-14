package com.jisungin.application.user.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserInfoResponse {

    private Long userId;

    private String userImage;

    private String userName;

    @Builder
    public UserInfoResponse(Long userId, String userImage, String userName) {
        this.userId = userId;
        this.userImage = userImage;
        this.userName = userName;
    }

    public static UserInfoResponse of(Long userId, String userName, String userImage) {
        return UserInfoResponse.builder()
                .userId(userId)
                .userImage(userImage)
                .userName(userName)
                .build();
    }

}
