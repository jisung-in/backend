package com.jisungin.application;

import static java.lang.Math.max;
import static java.lang.Math.min;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PageServiceRequest {

    private static final int MAX_SIZE = 2000;

    private Integer page;

    private Integer size;

    @Builder
    private PageServiceRequest(Integer page, Integer size) {
        this.page = page;
        this.size = size;
    }

    public long getOffset() {
        return (long) (max(1, page) - 1) * min(size, MAX_SIZE);
    }

}
