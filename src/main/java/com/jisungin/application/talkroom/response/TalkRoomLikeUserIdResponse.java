package com.jisungin.application.talkroom.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TalkRoomLikeUserIdResponse {

    private Long talkRoomId;
    private Long userId;

    @Builder
    @QueryProjection
    public TalkRoomLikeUserIdResponse(Long talkRoomId, Long userId) {
        this.talkRoomId = talkRoomId;
        this.userId = userId;
    }

}
