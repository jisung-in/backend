package com.jisungin.api.comment.request;

import com.jisungin.application.comment.request.CommentEditServiceRequest;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentEditRequest {

    private String content;

    private List<String> newImage = new ArrayList<>();

    private List<String> removeImage = new ArrayList<>();

    @Builder
    private CommentEditRequest(String content, List<String> newImage, List<String> removeImage) {
        this.content = content;
        this.newImage = newImage;
        this.removeImage = removeImage;
    }

    public CommentEditServiceRequest toService() {
        return CommentEditServiceRequest.builder()
                .content(content)
                .build();
    }

}
