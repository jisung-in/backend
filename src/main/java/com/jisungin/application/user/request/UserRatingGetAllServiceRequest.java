package com.jisungin.application.user.request;

import com.jisungin.domain.review.RatingOrderType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static java.lang.Math.*;

@Getter
@NoArgsConstructor
public class UserRatingGetAllServiceRequest {

    private static final int MAX_SIZE = 2_000;

    private Integer page;

    private Integer size;

    private RatingOrderType orderType;

    private Double rating;

    @Builder
    public UserRatingGetAllServiceRequest(Integer page, Integer size, RatingOrderType orderType, Double rating) {
        this.page = page;
        this.size = size;
        this.orderType = orderType;
        this.rating = rating;
    }

    public int getOffset() {
        return (max(1, page) - 1) * min(size, MAX_SIZE);
    }

}
