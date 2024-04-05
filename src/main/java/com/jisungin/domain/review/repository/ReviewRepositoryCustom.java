package com.jisungin.domain.review.repository;

import com.jisungin.application.PageResponse;
import com.jisungin.application.review.response.RatingFindAllResponse;
import com.jisungin.application.review.response.ReviewContentResponse;
import com.jisungin.domain.review.RatingOrderType;

public interface ReviewRepositoryCustom {

    PageResponse<RatingFindAllResponse> findAllRatingOrderBy(
            Long userId, RatingOrderType ratingSortType, Double rating, int size, int offset);

    PageResponse<ReviewContentResponse> findAllReviewContentOrderBy(
            Long userId, RatingOrderType orderType, int size, int offset);

}
