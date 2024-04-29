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

    @Builder
    private SearchRequest(Integer page, Integer size, String order, String query) {
        this.page = page != null ? page : 1;
        this.size = size != null ? size : 10;
        this.order = order != null ? order : "recent";
        this.query = query;
    }

    public SearchServiceRequest toService() {
        return SearchServiceRequest.builder()
                .page(page)
                .size(size)
                .order(order)
                .query(query)
                .build();
    }

}
