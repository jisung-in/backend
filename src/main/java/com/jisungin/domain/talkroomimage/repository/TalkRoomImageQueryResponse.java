package com.jisungin.domain.talkroomimage.repository;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TalkRoomImageQueryResponse {

    private Long talkRoomId;

    private String imageUrl;

    @Builder
    @QueryProjection
    public TalkRoomImageQueryResponse(Long talkRoomId, String imageUrl) {
        this.talkRoomId = talkRoomId;
        this.imageUrl = imageUrl;
    }

}
