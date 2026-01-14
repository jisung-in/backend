package com.jisungin.domain.review.repository;

import com.jisungin.application.PageResponse;
import com.jisungin.application.SliceResponse;
import com.jisungin.application.review.response.ReviewContentResponse;
import com.jisungin.application.review.response.ReviewWithRatingResponse;
import com.jisungin.domain.review.RatingOrderType;

public interface ReviewRepositoryCustom {

    PageResponse<ReviewContentResponse> findAllReviewContentOrderBy(
            Long userId, RatingOrderType orderType, int size, int offset);

    SliceResponse<ReviewWithRatingResponse> findAllByBookId(String isbn, Integer offset, Integer limit, String order);

    Long countByBookId(String isbn);

}
