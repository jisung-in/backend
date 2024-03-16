package com.jisungin.application.talkroom.response;

import com.jisungin.domain.ReadingStatus;
import com.jisungin.domain.talkroom.TalkRoom;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TalkRoomResponse {

    private Long id;
    private String content;
    private List<ReadingStatus> readingStatuses;

    @Builder
    private TalkRoomResponse(Long id, String content, List<ReadingStatus> readingStatuses) {
        this.id = id;
        this.content = content;
        this.readingStatuses = readingStatuses;
    }

    public static TalkRoomResponse of(TalkRoom talkRoom, List<ReadingStatus> readingStatuses) {
        return TalkRoomResponse.builder()
                .id(talkRoom.getId())
                .content(talkRoom.getContent())
                .readingStatuses(readingStatuses)
                .build();
    }

}
