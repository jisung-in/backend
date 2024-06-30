package com.jisungin.application.talkroom.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TalkRoomSearchCondition {

    private String search;
    private String day;

    @Builder
    private TalkRoomSearchCondition(String search, String day) {
        this.search = search;
        this.day = day;
    }

    public static TalkRoomSearchCondition of(String search, String day) {
        return TalkRoomSearchCondition.builder()
                .search(search)
                .day(day)
                .build();
    }

}
