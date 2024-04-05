package com.jisungin.application.talkroom.response;

import com.querydsl.core.annotations.QueryProjection;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TalkRoomFindOneResponse {

    private Long talkRoomId;
    private String userName;
    private String title;
    private String content;
    private String bookName;
    private String bookImage;
    private List<TalkRoomQueryReadingStatusResponse> readingStatuses = new ArrayList<>();
    private Long likeCount;
    private Long likeTalkRoomId;

    @Builder
    @QueryProjection
    public TalkRoomFindOneResponse(Long talkRoomId, String userName, String title, String content, String bookName,
                                   String bookImage, Long likeCount) {
        this.talkRoomId = talkRoomId;
        this.userName = userName;
        this.title = title;
        this.content = content;
        this.bookName = bookName;
        this.bookImage = bookImage;
        this.likeCount = likeCount;
    }

    public void addTalkRoomStatus(List<TalkRoomQueryReadingStatusResponse> readingStatuses) {
        this.readingStatuses = readingStatuses;
    }

    public void addTalkRoomLikeId(Long talkRoomId) {
        this.likeTalkRoomId = talkRoomId;
    }

}
