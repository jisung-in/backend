package com.jisungin.application.talkroomlike.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TalkRoomIds {

    private List<Long> talkRoomIds;

    @Builder
    private TalkRoomIds(List<Long> talkRoomIds) {
        this.talkRoomIds = talkRoomIds;
    }

    public static TalkRoomIds of(List<Long> talkRoomIds) {
        return TalkRoomIds.builder()
                .talkRoomIds(talkRoomIds)
                .build();
    }

}
