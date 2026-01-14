package com.jisungin.application.comment.response;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentQueryResponse {

    private Long commentId;
    private String userName;
    private String profileImage;
    private String content;
    private Long commentLikeCount;
    private LocalDateTime registeredDateTime;
    private Long creatorId;

    @Builder
    @QueryProjection
    public CommentQueryResponse(Long commentId, String userName, String profileImage, String content,
                                Long commentLikeCount,
                                LocalDateTime registeredDateTime, Long creatorId) {
        this.commentId = commentId;
        this.userName = userName;
        this.profileImage = profileImage;
        this.content = content;
        this.commentLikeCount = commentLikeCount;
        this.registeredDateTime = registeredDateTime;
        this.creatorId = creatorId;
    }

}
