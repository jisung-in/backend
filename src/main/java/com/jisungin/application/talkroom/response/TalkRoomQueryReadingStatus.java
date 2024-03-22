package com.jisungin.application.talkroom.response;

import com.jisungin.domain.ReadingStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TalkRoomQueryReadingStatus {

    private ReadingStatus readingStatuses;

    @Builder
    @QueryProjection
    public TalkRoomQueryReadingStatus(ReadingStatus readingStatuses) {
        this.readingStatuses = readingStatuses;
    }

}
