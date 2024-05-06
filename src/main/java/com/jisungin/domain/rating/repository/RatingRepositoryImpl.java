package com.jisungin.domain.rating.repository;

import com.jisungin.application.PageResponse;
import com.jisungin.application.rating.response.QRatingGetResponse;
import com.jisungin.application.rating.response.RatingGetResponse;
import com.jisungin.domain.review.RatingOrderType;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.jisungin.domain.rating.QRating.*;
import static com.jisungin.domain.review.RatingOrderType.*;

@RequiredArgsConstructor
public class RatingRepositoryImpl implements RatingRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public PageResponse<RatingGetResponse> getAllRatingOrderBy(
            Long userId, RatingOrderType ratingSortType, Double findRating, int size, int offset) {

        List<RatingGetResponse> ratings = queryFactory.select(new QRatingGetResponse(
                        rating1.book.isbn, rating1.book.title, rating1.book.imageUrl, rating1.rating))
                .from(rating1)
                //.leftJoin(book).on(rating1.book.eq(book));
                .where(rating1.user.id.eq(userId), ratingCondition(findRating))
                .groupBy(rating1.book.isbn)
                .orderBy(createSpecifier(ratingSortType), rating1.book.title.asc())
                .offset(offset)
                .limit(size)
                .fetch();

        return PageResponse.<RatingGetResponse>builder()
                .queryResponse(ratings)
                .totalCount(getTotalCount(userId, findRating)) // 해당 유저의 리뷰 총 개수, 쿼리 1회
                .size(size)
                .build();
    }

    private long getTotalCount(Long userId, Double findRating) {
        return queryFactory
                .select(rating1.count())
                .from(rating1)
                .where(rating1.user.id.eq(userId), ratingCondition(findRating))
                .fetchOne();
    }

    private OrderSpecifier createSpecifier(RatingOrderType ratingSortType) {
        if (ratingSortType.equals(RATING_ASC)) {
            return rating1.rating.asc();
        }
        if (ratingSortType.equals(RATING_DESC)) {
            return rating1.rating.desc();
        }
        if (ratingSortType.equals(RATING_AVG_ASC)) {
            return rating1.rating.avg().asc();
        }
        if (ratingSortType.equals(RATING_AVG_DESC)) {
            return rating1.rating.avg().desc();
        }

        return rating1.createDateTime.desc();
    }

    private BooleanExpression ratingCondition(Double rating) {
        if (rating == null) {
            return null;
        }
        return rating1.rating.eq(rating);
    }

}
