package com.jisungin.application.review.request;

import com.jisungin.domain.review.RatingOrderType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static java.lang.Math.max;
import static java.lang.Math.min;

@Getter
@NoArgsConstructor
public class ReviewContentGetAllServiceRequest {

    private static final int MAX_SIZE = 2_000;

    private Integer page;

    private Integer size;

    private RatingOrderType orderType;

    @Builder
    public ReviewContentGetAllServiceRequest(Integer page, Integer size, RatingOrderType orderType) {
        this.page = page;
        this.size = size;
        this.orderType = orderType;
    }

    public int getOffset() {
        return (max(1, page) - 1) * min(size, MAX_SIZE);
    }
}
