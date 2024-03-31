package com.jisungin.domain.review.repository;

import com.jisungin.application.PageResponse;
import com.jisungin.application.review.response.QRatingFindAllResponse;
import com.jisungin.application.review.response.RatingFindAllResponse;
import com.jisungin.domain.review.RatingOrderType;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static com.jisungin.domain.book.QBook.book;
import static com.jisungin.domain.review.QReview.review;
import static com.jisungin.domain.review.RatingOrderType.*;

@Slf4j
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public PageResponse<RatingFindAllResponse> findAllRatingOrderBy(
            Long userId, RatingOrderType ratingSortType, Double rating, int size, int offset) {
        log.info("--------------start--------------");
        // 리뷰 조회, 쿼리 1회
        List<RatingFindAllResponse> ratings = getRatings(userId, ratingSortType, rating, size, offset);

        return PageResponse.<RatingFindAllResponse>builder()
                .queryResponse(ratings)
                .totalCount(getTotalCount(userId, rating)) // 해당 유저의 리뷰 총 개수, 쿼리 1회
                .size(size)
                .build();
    }

    private List<RatingFindAllResponse> getRatings(
            Long userId, RatingOrderType ratingSortType, Double rating, int size, int offset) {
        return queryFactory
                .select(new QRatingFindAllResponse(
                        review.book.isbn, review.book.title, review.book.imageUrl, review.rating))
                .from(review)
                .leftJoin(book).on(review.book.eq(book))
                .where(review.user.id.eq(userId), ratingCondition(rating))
                .groupBy(review.book.isbn)
                .orderBy(createSpecifier(ratingSortType), review.id.asc())
                .offset(offset)
                .limit(size)
                .fetch();
    }

    private long getTotalCount(Long userId, Double rating) {
        return queryFactory
                .select(review.count())
                .from(review)
                .where(review.user.id.eq(userId), ratingCondition(rating))
                .fetchOne();
    }

    private OrderSpecifier createSpecifier(RatingOrderType ratingSortType) {
        if (ratingSortType.equals(RATING_ASC)) {
            return review.rating.asc();
        }
        if (ratingSortType.equals(RATING_DESC)) {
            return review.rating.desc();
        }
        if (ratingSortType.equals(RATING_AVG_ASC)) {
            return review.rating.avg().asc();
        }
        if (ratingSortType.equals(RATING_AVG_DESC)) {
            return review.rating.avg().desc();
        }

        return review.createDateTime.desc();
    }

    // 만약 별점 필터링 조건이 존재하면 해당하는 별점만 가져온다.
    private BooleanExpression ratingCondition(Double rating) {
        if (rating == null) {
            return null;
        }

        return review.rating.eq(rating);
    }

}
