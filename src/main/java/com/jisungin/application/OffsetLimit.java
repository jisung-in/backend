package com.jisungin.application;

import static java.lang.Math.*;

import lombok.Builder;
import lombok.Getter;

@Getter
public class OffsetLimit {

    private static final Integer MAX_SIZE = 2000;
    private Integer offset;
    private Integer limit;
    private String order;

    @Builder
    private OffsetLimit(Integer offset, Integer limit, String order) {
        this.offset = offset;
        this.limit = limit;
        this.order = order;
    }

    public static OffsetLimit of(Integer page, Integer size) {
        return OffsetLimit.builder()
                .offset(calculateOffset(page, size))
                .limit(size)
                .build();
    }

    public static OffsetLimit of(Integer page, Integer size, String order) {
        return OffsetLimit.builder()
                .offset(calculateOffset(page, size))
                .limit(size)
                .order(order)
                .build();
    }

    private static Integer calculateOffset(Integer page, Integer size) {
        return (max(1, page) - 1) * min(size, MAX_SIZE);
    }

}
