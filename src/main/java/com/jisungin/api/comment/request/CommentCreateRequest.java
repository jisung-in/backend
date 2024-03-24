package com.jisungin.api.comment.request;

import com.jisungin.application.comment.request.CommentCreateServiceRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentCreateRequest {

    @NotBlank(message = "내용은 필수 입니다.")
    private String content;

    @Builder
    private CommentCreateRequest(String content) {
        this.content = content;
    }

    public CommentCreateServiceRequest toService() {
        return CommentCreateServiceRequest.builder()
                .content(content)
                .build();
    }

}
