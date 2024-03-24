package com.jisungin.application.comment.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentCreateServiceRequest {

    private String content;

    @Builder
    private CommentCreateServiceRequest(String content) {
        this.content = content;
    }

}
