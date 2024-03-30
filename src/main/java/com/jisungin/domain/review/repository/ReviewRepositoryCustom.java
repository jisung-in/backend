package com.jisungin.domain.review.repository;

import com.jisungin.application.PageResponse;
import com.jisungin.application.review.response.RatingFindAllResponse;
import com.jisungin.domain.review.RatingOrderType;

public interface ReviewRepositoryCustom {

    PageResponse<RatingFindAllResponse> findAllRatingOrderBy(Long userId, RatingOrderType ratingSortType, int size, int offset);

}
