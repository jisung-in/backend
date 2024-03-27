package com.jisungin.application.talkroom.request;

import static java.lang.Math.max;
import static java.lang.Math.min;

import com.jisungin.application.OrderType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TalkRoomSearchServiceRequest {

    private static final int MAX_SIZE = 2000;

    private Integer page;

    private Integer size;

    private OrderType orderType;

    private String search;

    @Builder
    private TalkRoomSearchServiceRequest(Integer page, Integer size, OrderType orderType, String search) {
        this.page = page;
        this.size = size;
        this.orderType = orderType;
        this.search = search;
    }

    public long getOffset() {
        return (long) (max(1, page) - 1) * min(size, MAX_SIZE);
    }

}
