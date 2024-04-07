package com.jisungin.domain.review.repository;

import com.jisungin.application.PageResponse;
import com.jisungin.application.review.response.QRatingFindAllResponse;
import com.jisungin.application.review.response.QReviewContentResponse;
import com.jisungin.application.review.response.RatingFindAllResponse;
import com.jisungin.application.review.response.ReviewContentResponse;
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
import static com.jisungin.domain.user.QUser.user;

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

    @Override
    public PageResponse<ReviewContentResponse> findAllReviewContentOrderBy(
            Long userId, RatingOrderType orderType, int size, int offset) {
        log.info("--------------start--------------");
        // 리뷰 내용을 가져온다. 쿼리 1회
        List<ReviewContentResponse> reviewContents = getReviewContents(userId, orderType, size, offset);

        return PageResponse.<ReviewContentResponse>builder()
                .queryResponse(reviewContents)
                .totalCount(getTotalCount(userId, null)) // 리뷰 전체 개수, 쿼리 1회
                .size(size)
                .build();
    }

    private List<ReviewContentResponse> getReviewContents(
            Long userId, RatingOrderType orderType, int size, int offset) {
        return queryFactory
                .select(new QReviewContentResponse(
                        review.id, user.profileImage, user.name, review.rating, review.content,
                        book.isbn, book.title, book.imageUrl
                ))
                .from(review)
                .leftJoin(book).on(review.book.eq(book))
                .leftJoin(book).on(review.user.eq(user))
                .where(review.user.id.eq(userId))
                .orderBy(createSpecifier(orderType), review.id.asc())
                .offset(offset)
                .limit(size)
                .fetch();
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
