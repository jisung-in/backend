package com.jisungin.api.comment.request;

import com.jisungin.application.comment.request.CommentEditServiceRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentEditRequest {

    private String content;

    @Builder
    private CommentEditRequest(String content) {
        this.content = content;
    }

    public CommentEditServiceRequest toService() {
        return CommentEditServiceRequest.builder()
                .content(content)
                .build();
    }

}
