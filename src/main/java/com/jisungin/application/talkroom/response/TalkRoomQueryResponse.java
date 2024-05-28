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
    private String bookAuthor;
    private String bookThumbnail;
    private Long likeCount;
    private LocalDateTime registeredDateTime;
    private Long creatorId;

    @Builder
    @QueryProjection
    public TalkRoomQueryResponse(Long id, String profileImage, String username, String title, String content,
                                 String bookName, String bookAuthor, String bookThumbnail, Long likeCount,
                                 LocalDateTime registeredDateTime, Long creatorId) {
        this.id = id;
        this.profileImage = profileImage;
        this.username = username;
        this.title = title;
        this.content = content;
        this.bookName = bookName;
        this.bookAuthor = bookAuthor;
        this.bookThumbnail = bookThumbnail;
        this.likeCount = likeCount;
        this.registeredDateTime = registeredDateTime;
        this.creatorId = creatorId;
    }

}
