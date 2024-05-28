package com.jisungin.application.talkroom.response;

import com.jisungin.domain.ReadingStatus;
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
    private Long creatorId;

    @Builder
    private TalkRoomFindAllResponse(Long id, String profileImage, String username, String title, String content,
                                    String bookName, String bookAuthor,
                                    String bookThumbnail, Long likeCount, List<String> readingStatuses,
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
        this.readingStatuses = readingStatuses;
        this.registeredDateTime = registeredDateTime;
        this.creatorId = creatorId;
    }

    public static TalkRoomFindAllResponse of(TalkRoomQueryResponse talkRoom, List<ReadingStatus> readingStatuses) {
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
                .readingStatuses(extractReadingStatuses(readingStatuses))
                .registeredDateTime(talkRoom.getRegisteredDateTime().withNano(0))
                .creatorId(talkRoom.getCreatorId())
                .build();
    }

    public static List<TalkRoomFindAllResponse> toList(List<TalkRoomQueryResponse> talkRooms,
                                                       Map<Long, List<ReadingStatus>> readingStatuses) {
        return talkRooms.stream()
                .map(talkRoom -> TalkRoomFindAllResponse.of(talkRoom, readingStatuses.get(talkRoom.getId())))
                .toList();
    }

    private static List<String> extractReadingStatuses(List<ReadingStatus> readingStatuses) {
        return readingStatuses.stream()
                .map(ReadingStatus::getText)
                .toList();
    }

}
