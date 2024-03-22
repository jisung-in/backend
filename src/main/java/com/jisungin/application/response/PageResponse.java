package com.jisungin.application.response;

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

    @Builder
    private PageResponse(List<T> queryResponse, long totalCount, int size) {
        this.queryResponse = queryResponse;
        this.totalCount = totalCount;
        this.size = size;
    }

}
