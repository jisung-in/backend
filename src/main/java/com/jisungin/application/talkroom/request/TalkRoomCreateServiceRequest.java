package com.jisungin.application.talkroom.request;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TalkRoomCreateServiceRequest {

    private Long bookId;

    private String content;

    private List<String> readingStatus;

    @Builder
    private TalkRoomCreateServiceRequest(Long bookId, String content, List<String> readingStatus) {
        this.bookId = bookId;
        this.content = content;
        this.readingStatus = readingStatus;
    }
}
