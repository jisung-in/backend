package com.jisungin.api;

import com.jisungin.application.OrderType;
import com.jisungin.application.SearchServiceRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SearchRequest {

    private Integer page = 1;

    private Integer size = 10;

    private String order;

    private String search;

    @Builder
    private SearchRequest(Integer page, Integer size, String order, String search) {
        this.page = page != null ? page : 1;
        this.size = size != null ? size : 1;
        this.order = order != null ? order : "recent";
        this.search = search;
    }

    public SearchServiceRequest toService() {
        return SearchServiceRequest.builder()
                .page(page)
                .size(size)
                .search(search)
                .orderType(OrderType.convertToOrderType(order))
                .build();
    }

}
