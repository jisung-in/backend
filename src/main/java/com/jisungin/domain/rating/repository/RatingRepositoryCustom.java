package com.jisungin.domain.rating.repository;

import com.jisungin.application.PageResponse;
import com.jisungin.application.rating.response.RatingGetResponse;
import com.jisungin.domain.review.RatingOrderType;

public interface RatingRepositoryCustom {

    PageResponse<RatingGetResponse> getAllRatingOrderBy(
            Long userId, RatingOrderType ratingSortType, Double rating, int size, int offset);

}
