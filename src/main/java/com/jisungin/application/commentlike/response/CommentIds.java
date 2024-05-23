package com.jisungin.application.commentlike.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CommentIds {

    private List<Long> commentIds;

    @Builder
    private CommentIds(List<Long> commentIds) {
        this.commentIds = commentIds;
    }

    public static CommentIds of(List<Long> commentIds) {
        return CommentIds.builder()
                .commentIds(commentIds)
                .build();
    }

}
