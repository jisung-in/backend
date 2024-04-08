package com.jisungin.application.book.response;

import com.jisungin.application.talkroom.response.TalkRoomQueryResponse;
import com.jisungin.domain.ReadingStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
public class BookRelatedTalkRoomResponse {

    private Long id;
    private String profileImage;
    private String username;
    private String title;
    private String bookName;
    private String bookThumbnail;
    private Long likeCount;
    private List<String> readingStatuses = new ArrayList<>();

    @Builder
    public BookRelatedTalkRoomResponse(Long id, String profileImage, String username, String title, String bookName,
                                       String bookThumbnail, Long likeCount, List<String> readingStatuses) {
        this.id = id;
        this.profileImage = profileImage;
        this.username = username;
        this.title = title;
        this.bookName = bookName;
        this.bookThumbnail = bookThumbnail;
        this.likeCount = likeCount;
        this.readingStatuses = readingStatuses;
    }

    public static List<BookRelatedTalkRoomResponse> create(List<TalkRoomQueryResponse> talkRooms,
                                                           Map<Long, List<ReadingStatus>> readingStatuses) {
        return talkRooms.stream()
                .map(talkRoom -> {
                    List<String> talkRoomReadingStatus = extractReadingStatuses(readingStatuses, talkRoom);

                    return BookRelatedTalkRoomResponse.builder()
                            .id(talkRoom.getId())
                            .profileImage(talkRoom.getProfileImage())
                            .username(talkRoom.getUsername())
                            .title(talkRoom.getTitle())
                            .bookName(talkRoom.getBookName())
                            .bookThumbnail(talkRoom.getBookThumbnail())
                            .likeCount(talkRoom.getLikeCount())
                            .readingStatuses(talkRoomReadingStatus)
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
