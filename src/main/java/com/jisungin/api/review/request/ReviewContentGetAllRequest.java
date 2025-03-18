package com.jisungin.api.review.request;

import com.jisungin.application.review.request.ReviewContentGetAllServiceRequest;
import com.jisungin.domain.review.RatingOrderType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReviewContentGetAllRequest {

    private Integer page;

    private Integer size;

    private String order;

    @Builder
    public ReviewContentGetAllRequest(Integer page, Integer size, String order) {
        this.page = page != null ? page : 1;
        this.size = size != null ? size : 10;
        this.order = order != null ? order : "date";
    }

    public ReviewContentGetAllServiceRequest toService() {
        return ReviewContentGetAllServiceRequest.builder()
                .page(page)
                .size(size)
                .orderType(RatingOrderType.fromName(order))
                .build();
    }
}
