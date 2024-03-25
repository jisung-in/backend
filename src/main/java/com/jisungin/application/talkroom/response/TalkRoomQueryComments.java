package com.jisungin.application.talkroom.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TalkRoomQueryComments {

    private Long commentId;
    private String userName;
    private String content;

    @Builder
    @QueryProjection
    public TalkRoomQueryComments(Long commentId, String userName, String content) {
        this.commentId = commentId;
        this.userName = userName;
        this.content = content;
    }

}
