package com.jisungin.application.talkroom.response;

import com.jisungin.domain.ReadingStatus;
import com.querydsl.core.annotations.QueryProjection;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TalkRoomResponse {

    private String userName;
    private String title;
    private String bookName;
    private String content;
    private List<ReadingStatus> readingStatuses;
    private String bookImage;

    @Builder
    @QueryProjection
    public TalkRoomResponse(String userName, String title, String content, String bookName,
                            List<ReadingStatus> readingStatuses, String bookImage) {
        this.userName = userName;
        this.title = title;
        this.content = content;
        this.bookName = bookName;
        this.readingStatuses = readingStatuses;
        this.bookImage = bookImage;
    }

    public static TalkRoomResponse of(String userName, String title, String content,
                                      List<ReadingStatus> readingStatuses,
                                      String bookImage, String bookName) {
        return TalkRoomResponse.builder()
                .userName(userName)
                .title(title)
                .content(content)
                .bookName(bookName)
                .readingStatuses(readingStatuses)
                .bookImage(bookImage)
                .build();
    }

}
