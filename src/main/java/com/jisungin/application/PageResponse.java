package com.jisungin.application;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PageResponse<T> {

    private List<T> queryResponse = new ArrayList<>();

    private long totalCount;

    private int size;

    private List<Long> likeContents;

    @Builder
    private PageResponse(List<T> queryResponse, long totalCount, int size) {
        this.queryResponse = queryResponse;
        this.totalCount = totalCount;
        this.size = size;
    }

    public void addContents(List<Long> contents) {
        this.likeContents = contents;
    }

    public static <T> PageResponse <T> of(int size, long totalCount, List<T> queryResponse) {
        return PageResponse.<T>builder()
                .size(size)
                .totalCount(totalCount)
                .queryResponse(queryResponse)
                .build();
    }

}
