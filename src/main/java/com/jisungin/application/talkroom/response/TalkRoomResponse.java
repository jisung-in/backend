package com.jisungin.application.talkroom.response;

import com.jisungin.domain.ReadingStatus;
import com.jisungin.domain.talkroom.TalkRoom;
import com.querydsl.core.annotations.QueryProjection;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TalkRoomResponse {

    private String userName;
    private String content;
    private List<ReadingStatus> readingStatuses;
    private String bookImage;

    @Builder
    @QueryProjection
    public TalkRoomResponse(String userName, String content, List<ReadingStatus> readingStatuses, String bookImage) {
        this.userName = userName;
        this.content = content;
        this.readingStatuses = readingStatuses;
        this.bookImage = bookImage;
    }

    public static TalkRoomResponse of(String userName, TalkRoom talkRoom, List<ReadingStatus> readingStatuses,
                                      String bookImage) {
        return TalkRoomResponse.builder()
                .userName(userName)
                .content(talkRoom.getContent())
                .readingStatuses(readingStatuses)
                .bookImage(bookImage)
                .build();
    }

}
