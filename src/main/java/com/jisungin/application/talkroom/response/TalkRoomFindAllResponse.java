package com.jisungin.application.talkroom.response;

import com.jisungin.domain.ReadingStatus;
import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TalkRoomFindAllResponse {

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

    @Builder
    @QueryProjection
    public TalkRoomFindAllResponse(Long id, String profileImage, String username, String title, String content,
                                   String bookName, String bookAuthor,
                                   String bookThumbnail, Long likeCount, List<String> readingStatuses,
                                   LocalDateTime registeredDateTime) {
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
    }

    public static List<TalkRoomFindAllResponse> create(List<TalkRoomQueryResponse> talkRooms,
                                                       Map<Long, List<ReadingStatus>> readingStatuses) {
        return talkRooms.stream()
                .map(talkRoom -> {
                    List<String> talkRoomReadingStatus = extractReadingStatuses(readingStatuses, talkRoom);

                    return TalkRoomFindAllResponse.builder()
                            .id(talkRoom.getId())
                            .profileImage(talkRoom.getProfileImage())
                            .username(talkRoom.getUsername())
                            .title(talkRoom.getTitle())
                            .content(talkRoom.getContent())
                            .bookName(talkRoom.getBookName())
                            .bookAuthor(talkRoom.getBookAuthor())
                            .bookThumbnail(talkRoom.getBookThumbnail())
                            .likeCount(talkRoom.getLikeCount())
                            .readingStatuses(talkRoomReadingStatus)
                            .registeredDateTime(talkRoom.getRegisteredDateTime().withNano(0))
                            .build();
                })
                .toList();
    }

    private static List<String> extractReadingStatuses(Map<Long, List<ReadingStatus>> readingStatuses,
                                                       TalkRoomQueryResponse talkRoom) {
        return readingStatuses.get(talkRoom.getId()).stream()
                .map(ReadingStatus::getText)
                .toList();
    }

}
