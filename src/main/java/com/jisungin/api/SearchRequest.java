package com.jisungin.api;

import com.jisungin.application.SearchServiceRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SearchRequest {

    private Integer page = 1;

    private Integer size = 10;

    private String order;

    private String query;

    private String day;

    @Builder
    private SearchRequest(Integer page, Integer size, String order, String query, String day) {
        this.page = page != null ? page : 1;
        this.size = size != null ? size : 10;
        this.order = order != null ? order : "recent";
        this.query = query;
        this.day = day;
    }

    public SearchServiceRequest toService() {
        return SearchServiceRequest.builder()
                .page(page)
                .size(size)
                .order(order)
                .query(query)
                .day(day)
                .build();
    }

}
