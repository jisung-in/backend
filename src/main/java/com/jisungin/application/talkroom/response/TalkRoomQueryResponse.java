package com.jisungin.application.talkroom.response;

import com.querydsl.core.annotations.QueryProjection;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TalkRoomQueryResponse {

    private Long talkRoomId;
    private String userName;
    private String title;
    private String content;
    private String bookName;
    private String bookImage;
    private List<TalkRoomQueryReadingStatus> readingStatuses = new ArrayList<>();

    @Builder
    @QueryProjection
    public TalkRoomQueryResponse(Long talkRoomId, String userName, String title, String content, String bookName,
                                 String bookImage) {
        this.talkRoomId = talkRoomId;
        this.userName = userName;
        this.title = title;
        this.content = content;
        this.bookName = bookName;
        this.bookImage = bookImage;
    }

    public void addTalkRoomStatus(List<TalkRoomQueryReadingStatus> readingStatuses) {
        this.readingStatuses = readingStatuses;
    }

}
