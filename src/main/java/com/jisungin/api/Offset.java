package com.jisungin.api;

import lombok.Builder;

public class Offset {

    private static final int MAX_SIZE = 2000;

    private Integer page;

    private Integer size;

    @Builder
    private Offset(Integer page, Integer size) {
        this.page = page;
        this.size = size;
    }

    public static long of(Integer page, Integer size) {
        return (long) (Math.max(1, page) - 1) * Math.min(size, MAX_SIZE);
    }

}
