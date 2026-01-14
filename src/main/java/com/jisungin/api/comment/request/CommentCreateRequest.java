package com.jisungin.api.comment.request;

import com.jisungin.application.comment.request.CommentCreateServiceRequest;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentCreateRequest {

    @NotBlank(message = "내용은 필수 입니다.")
    private String content;

    private List<String> imageUrls = new ArrayList<>();

    @Builder
    private CommentCreateRequest(String content, List<String> imageUrls) {
        this.content = content;
        this.imageUrls = imageUrls;
    }

    public CommentCreateServiceRequest toService() {
        return CommentCreateServiceRequest.builder()
                .content(content)
                .imageUrls(imageUrls)
                .build();
    }

}
