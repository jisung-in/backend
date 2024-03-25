package com.jisungin.application.comment.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentEditServiceRequest {

    private String content;

    @Builder
    private CommentEditServiceRequest(String content) {
        this.content = content;
    }

}
