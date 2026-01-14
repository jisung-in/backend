package com.jisungin.application.comment.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentLikeUserIdResponse {

    private Long commentId;

    private Long userId;

    @Builder
    @QueryProjection
    public CommentLikeUserIdResponse(Long commentId, Long userId) {
        this.commentId = commentId;
        this.userId = userId;
    }

}

