package com.jisungin.application.comment.request;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentCreateServiceRequest {

    private String content;

    private List<String> imageUrls = new ArrayList<>();

    @Builder
    private CommentCreateServiceRequest(String content, List<String> imageUrls) {
        this.content = content;
        this.imageUrls = imageUrls;
    }

}
