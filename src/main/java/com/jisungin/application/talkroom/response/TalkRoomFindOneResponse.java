package com.jisungin.application.talkroom.response;

import com.jisungin.domain.ReadingStatus;
import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TalkRoomFindOneResponse {

    private Long id;
    private String profileImage;
    private String username;
    private String title;
    private String content;
    private String bookName;
    private String bookAuthor;
    private String bookThumbnail;
    private Long likeCount;
    private List<String> readingStatuses = new ArrayList<>();
    private LocalDateTime registeredDateTime;
    private List<String> images = new ArrayList<>();
    private boolean likeTalkRoom;

    @Builder
    @QueryProjection
    public TalkRoomFindOneResponse(Long id, String profileImage, String username, String title, String content,
                                   String bookName, String bookAuthor, String bookThumbnail, Long likeCount,
                                   List<String> readingStatuses,
                                   LocalDateTime registeredDateTime, List<String> images, boolean likeTalkRoom) {
        this.id = id;
        this.profileImage = profileImage;
        this.username = username;
        this.title = title;
        this.content = content;
        this.bookName = bookName;
        this.bookAuthor = bookAuthor;
        this.bookThumbnail = bookThumbnail;
        this.likeCount = likeCount;
        this.readingStatuses = readingStatuses;
        this.registeredDateTime = registeredDateTime;
        this.images = images;
        this.likeTalkRoom = likeTalkRoom;
    }

    public static TalkRoomFindOneResponse create(TalkRoomQueryResponse talkRoom,
                                                 List<ReadingStatus> readingStatuses, List<String> images,
                                                 boolean exists) {
        return TalkRoomFindOneResponse.builder()
                .id(talkRoom.getId())
                .profileImage(talkRoom.getProfileImage())
                .username(talkRoom.getUsername())
                .title(talkRoom.getTitle())
                .content(talkRoom.getContent())
                .bookName(talkRoom.getBookName())
                .bookAuthor(talkRoom.getBookAuthor())
                .bookThumbnail(talkRoom.getBookThumbnail())
                .likeCount(talkRoom.getLikeCount())
                .readingStatuses(extractReadingStatuses(readingStatuses))
                .registeredDateTime(talkRoom.getRegisteredDateTime())
                .images(images)
                .likeTalkRoom(exists)
                .build();
    }

    public static TalkRoomFindOneResponse create(Long id, String profileImage, String username, String title,
                                                 String content, String bookName, String bookAuthor,
                                                 String bookThumbnail,
                                                 List<ReadingStatus> readingStatuses, LocalDateTime registeredDateTime,
                                                 List<String> images) {
        return TalkRoomFindOneResponse.builder()
                .id(id)
                .profileImage(profileImage)
                .username(username)
                .title(title)
                .content(content)
                .bookName(bookName)
                .bookAuthor(bookAuthor)
                .bookThumbnail(bookThumbnail)
                .likeCount(0L)
                .readingStatuses(extractReadingStatuses(readingStatuses))
                .registeredDateTime(registeredDateTime)
                .images(images)
                .likeTalkRoom(false)
                .build();
    }

    private static List<String> extractReadingStatuses(List<ReadingStatus> readingStatuses) {
        return readingStatuses.stream()
                .map(ReadingStatus::getText)
                .toList();
    }

}
