package com.jisungin.application.talkroom.response;

import com.jisungin.application.PageResponse;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TalkRoomPageResponse {
    private PageResponse<TalkRoomFindAllResponse> response;
    private List<Long> userLikeTalkRoomIds = new ArrayList<>();

    @Builder
    private TalkRoomPageResponse(PageResponse<TalkRoomFindAllResponse> response, List<Long> userLikeTalkRoomIds) {
        this.response = response;
        this.userLikeTalkRoomIds = userLikeTalkRoomIds;
    }

    public static TalkRoomPageResponse of(PageResponse<TalkRoomFindAllResponse> response,
                                          List<Long> userLikeTalkRoomIds) {
        return TalkRoomPageResponse.builder()
                .response(response)
                .userLikeTalkRoomIds(userLikeTalkRoomIds)
                .build();
    }

}
