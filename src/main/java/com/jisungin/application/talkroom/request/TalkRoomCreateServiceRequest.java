package com.jisungin.application.talkroom.request;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TalkRoomCreateServiceRequest {

    private String bookIsbn;

    private String title;

    private String content;

    private List<String> readingStatus;

    private List<String> imageUrls;

    @Builder
    private TalkRoomCreateServiceRequest(String bookIsbn, String title, String content, List<String> readingStatus,
                                         List<String> imageUrls) {
        this.bookIsbn = bookIsbn;
        this.title = title;
        this.content = content;
        this.readingStatus = readingStatus;
        this.imageUrls = imageUrls;
    }

}
