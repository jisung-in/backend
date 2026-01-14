package com.jisungin.application.comment.request;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentEditServiceRequest {

    private String content;

    private List<String> newImage = new ArrayList<>();

    private List<String> removeImage = new ArrayList<>();

    @Builder
    private CommentEditServiceRequest(String content, List<String> newImage, List<String> removeImage) {
        this.content = content;
        this.newImage = newImage;
        this.removeImage = removeImage;
    }

}
