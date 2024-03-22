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

    private ReadingStatus readingStatuses;

    @Builder
    @QueryProjection
    public TalkRoomQueryReadingStatus(Long talkRoomId, ReadingStatus readingStatuses) {
        this.talkRoomId = talkRoomId;
        this.readingStatuses = readingStatuses;
    }

}
