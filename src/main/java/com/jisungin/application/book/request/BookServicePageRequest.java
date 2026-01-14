package com.jisungin.application.book.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class BookServicePageRequest {

    private Integer page;
    private Integer size;

    @Builder
    private BookServicePageRequest(Integer page, Integer size) {
        this.page = page;
        this.size = size;
    }

    public Integer extractStartIndex() {
        return (page * size) - size + 1;
    }

    public Integer extractEndIndex() {
        return page * size;
    }

    public long getOffset() {
        return (long) (Math.max(1, this.page) - 1) * Math.min(this.size, 2000);
    }

}
