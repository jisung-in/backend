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
    private List<TalkRoomQueryCommentsResponse> comments = new ArrayList<>();
    private Long likeCount;
    private Long commentCount;
    private List<TalkRoomLikeUserIdResponse> userIds = new ArrayList<>();

    @Builder
    @QueryProjection
    public TalkRoomFindOneResponse(Long talkRoomId, String userName, String title, String content, String bookName,
                                   String bookImage) {
        this.talkRoomId = talkRoomId;
        this.userName = userName;
        this.title = title;
        this.content = content;
        this.bookName = bookName;
        this.bookImage = bookImage;
    }

    public void addTalkRoomStatus(List<TalkRoomQueryReadingStatusResponse> readingStatuses) {
        this.readingStatuses = readingStatuses;
    }

    public void addTalkRoomComments(List<TalkRoomQueryCommentsResponse> comments) {
        this.comments = comments;
    }

    public void addLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }

    public void addCommentCount(Long commentCount) {
        this.commentCount = commentCount;
    }

    public void addUserIds(List<TalkRoomLikeUserIdResponse> userIds) {
        this.userIds = userIds;
    }

}
