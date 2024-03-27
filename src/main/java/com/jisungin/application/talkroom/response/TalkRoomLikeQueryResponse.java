package com.jisungin.application.talkroom.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TalkRoomLikeQueryResponse {

    private Long talkRoomId;
    private Long likeCount;

    @Builder
    @QueryProjection
    public TalkRoomLikeQueryResponse(Long talkRoomId, Long likeCount) {
        this.talkRoomId = talkRoomId;
        this.likeCount = likeCount;
    }

}
