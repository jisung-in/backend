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

    private String isbn;

    @Builder
    private CommentCreateServiceRequest(String content, List<String> imageUrls, String isbn) {
        this.content = content;
        this.imageUrls = imageUrls;
        this.isbn = isbn;
    }

}
