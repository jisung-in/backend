package com.jisungin.application.talkroom.request;

import static java.lang.Math.max;
import static java.lang.Math.min;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TalkRoomSearchServiceRequest {

    private static final int MAX_SIZE = 2000;

    private Integer page;

    private Integer size;

    private String order;

    @Builder
    private TalkRoomSearchServiceRequest(Integer page, Integer size, String order) {
        this.page = page;
        this.size = size;
        this.order = order;
    }

    public long getOffset() {
        return (long) (max(1, page) - 1) * min(size, MAX_SIZE);
    }

}
