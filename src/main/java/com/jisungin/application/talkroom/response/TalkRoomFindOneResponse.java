package com.jisungin.application.talkroom.response;

import com.jisungin.domain.ReadingStatus;
import com.jisungin.domain.book.Book;
import com.jisungin.domain.talkroom.TalkRoom;
import com.jisungin.domain.user.User;
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
    private Long creatorId;

    @Builder
    @QueryProjection
    public TalkRoomFindOneResponse(Long id, String profileImage, String username, String title, String content,
                                   String bookName, String bookAuthor, String bookThumbnail, Long likeCount,
                                   List<String> readingStatuses,
                                   LocalDateTime registeredDateTime, List<String> images, Long creatorId) {
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
        this.creatorId = creatorId;
    }

    public static TalkRoomFindOneResponse of(TalkRoom talkRoom, Book book, User user, List<String> imageUrls,
                                             List<ReadingStatus> readingStatuses) {
        return TalkRoomFindOneResponse.builder()
                .id(talkRoom.getId())
                .profileImage(user.getProfileImage())
                .username(user.getName())
                .title(talkRoom.getTitle())
                .content(talkRoom.getContent())
                .bookName(book.getTitle())
                .bookAuthor(book.getAuthors())
                .bookThumbnail(book.getThumbnail())
                .readingStatuses(extractReadingStatuses(readingStatuses))
                .registeredDateTime(talkRoom.getRegisteredDateTime())
                .images(imageUrls)
                .likeCount(0L)
                .creatorId(user.getId())
                .build();
    }

    public static TalkRoomFindOneResponse of(TalkRoomQueryResponse talkRoom, List<String> images,
                                             List<ReadingStatus> readingStatuses) {
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
                .creatorId(talkRoom.getCreatorId())
                .build();
    }

    private static List<String> extractReadingStatuses(List<ReadingStatus> readingStatuses) {
        return readingStatuses.stream()
                .map(ReadingStatus::getText)
                .toList();
    }

}
