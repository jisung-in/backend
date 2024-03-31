package com.jisungin.api.user.request;

import com.jisungin.application.user.request.UserRatingGetAllServiceRequest;
import com.jisungin.domain.review.RatingOrderType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class UserRatingGetAllRequest {

    private Integer page;

    private Integer size;

    private String order;

    private Double rating;

    @Builder
    public UserRatingGetAllRequest(Integer page, Integer size, String order, String rating) {
        this.page = page != null ? page : 1;
        this.size = size != null ? size : 10;
        this.order = order != null ? order : "date";
        this.rating = rating != null ? Double.parseDouble(rating) : null;
    }

    public UserRatingGetAllServiceRequest toService() {
        return UserRatingGetAllServiceRequest.builder()
                .page(page)
                .size(size)
                .orderType(RatingOrderType.fromName(order))
                .rating(rating)
                .build();
    }

}
