package com.jisungin.application.talkroom.response;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TalkRoomPageResponse {

    private List<TalkRoomQueryResponse> talkRoomQueryResponses = new ArrayList<>();

    private long totalCount;

    @Builder
    private TalkRoomPageResponse(List<TalkRoomQueryResponse> talkRoomQueryResponses, long totalCount) {
        this.talkRoomQueryResponses = talkRoomQueryResponses;
        this.totalCount = totalCount;
    }

    public static long addTotalCountPage(long totalCount, Integer size) {
        return (long) (Math.ceil((double) totalCount / size));
    }

}
