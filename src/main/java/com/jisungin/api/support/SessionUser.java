package com.jisungin.api.support;

import com.jisungin.domain.user.User;
import java.io.Serializable;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SessionUser implements Serializable {

    private Long id;
    private String name;
    private String email;
    private String picture;

    @Builder
    public SessionUser(Long id, String name, String email, String picture) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.picture = picture;
    }

    public static SessionUser of(User user) {
        return SessionUser.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .picture(user.getProfileImage())
                .build();
    }

}
