package com.jisungin.application;

import static java.lang.Math.max;
import static java.lang.Math.min;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SearchServiceRequest {

    private static final int MAX_SIZE = 2000;

    private Integer page;

    private Integer size;

    private OrderType orderType;

    private String search;

    @Builder
    private SearchServiceRequest(Integer page, Integer size, OrderType orderType, String search) {
        this.page = page;
        this.size = size;
        this.orderType = orderType;
        this.search = search;
    }

    public long getOffset() {
        return (long) (max(1, page) - 1) * min(size, MAX_SIZE);
    }

}
