package com.jisungin.application.comment.response;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentResponse {

    private String content;

    private String userName;

    private List<String> imageUrls = new ArrayList<>();

    @Builder
    private CommentResponse(String content, String userName, List<String> imageUrls) {
        this.content = content;
        this.userName = userName;
        this.imageUrls = imageUrls;
    }

    public static CommentResponse of(String content, String name, List<String> imageUrls) {
        return CommentResponse.builder()
                .content(content)
                .userName(name)
                .imageUrls(imageUrls)
                .build();
    }

}
