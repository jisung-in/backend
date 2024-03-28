package com.jisungin.application.comment.response;

import com.querydsl.core.annotations.QueryProjection;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentQueryResponse {

    private Long commentId;
    private String userName;
    private String content;
    private Long commentLikeCount;
    private List<CommentLikeUserIdResponse> userIds = new ArrayList<>();

    @Builder
    @QueryProjection
    public CommentQueryResponse(Long commentId, String userName, String content, Long commentLikeCount) {
        this.commentId = commentId;
        this.userName = userName;
        this.content = content;
        this.commentLikeCount = commentLikeCount;
    }

    public void addLikeUserIds(List<CommentLikeUserIdResponse> userIds) {
        this.userIds = userIds;
    }

}
