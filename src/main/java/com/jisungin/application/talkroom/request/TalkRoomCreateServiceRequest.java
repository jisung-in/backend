package com.jisungin.application.talkroom.request;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TalkRoomCreateServiceRequest {

    private String bookIsbn;

    private String content;

    private List<String> readingStatus;

    @Builder
    private TalkRoomCreateServiceRequest(String bookIsbn, String content, List<String> readingStatus) {
        this.bookIsbn = bookIsbn;
        this.content = content;
        this.readingStatus = readingStatus;
    }

}
