package com.jisungin.application.talkroom.response;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TalkRoomQueryResponse {

    private Long id;
    private String profileImage;
    private String username;
    private String title;
    private String content;
    private String bookName;
    private String bookThumbnail;
    private Long likeCount;
    private LocalDateTime createTime;

    @Builder
    @QueryProjection
    public TalkRoomQueryResponse(Long id, String profileImage, String username, String title, String content,
                                 String bookName, String bookThumbnail, Long likeCount, LocalDateTime createTime) {
        this.id = id;
        this.profileImage = profileImage;
        this.username = username;
        this.title = title;
        this.content = content;
        this.bookName = bookName;
        this.bookThumbnail = bookThumbnail;
        this.likeCount = likeCount;
        this.createTime = createTime;
    }

}
