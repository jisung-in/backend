package com.jisungin.application.talkroom.response;

import com.jisungin.domain.ReadingStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TalkRoomQueryReadingStatus {

    private Long talkRoomId;

    private ReadingStatus readingStatus;

    @Builder
    @QueryProjection
    public TalkRoomQueryReadingStatus(Long talkRoomId, ReadingStatus readingStatus) {
        this.talkRoomId = talkRoomId;
        this.readingStatus = readingStatus;
    }

}
