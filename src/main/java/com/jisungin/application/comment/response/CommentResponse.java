package com.jisungin.application.comment.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentResponse {

    private String content;

    private String userName;

    @Builder
    private CommentResponse(String content, String userName) {
        this.content = content;
        this.userName = userName;
    }

    public static CommentResponse of(String content, String name) {
        return CommentResponse.builder()
                .content(content)
                .userName(name)
                .build();
    }

}
