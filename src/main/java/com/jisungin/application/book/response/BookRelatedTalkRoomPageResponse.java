package com.jisungin.application.book.response;

import com.jisungin.application.PageResponse;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class BookRelatedTalkRoomPageResponse {

    PageResponse<BookRelatedTalkRoomResponse> response;
    List<Long> userLikeTalkRoomIds = new ArrayList<>();

    @Builder
    private BookRelatedTalkRoomPageResponse(PageResponse<BookRelatedTalkRoomResponse> response,
                                           List<Long> userLikeTalkRoomIds) {
        this.response = response;
        this.userLikeTalkRoomIds = userLikeTalkRoomIds;
    }

    public static BookRelatedTalkRoomPageResponse of(PageResponse<BookRelatedTalkRoomResponse> response,
                                                     List<Long> userLikeTalkRoomIds) {
        return BookRelatedTalkRoomPageResponse.builder()
                .response(response)
                .userLikeTalkRoomIds(userLikeTalkRoomIds)
                .build();
    }

}
