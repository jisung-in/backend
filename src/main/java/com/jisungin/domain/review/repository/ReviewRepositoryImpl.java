package com.jisungin.domain.review.repository;

import com.jisungin.application.PageResponse;
import com.jisungin.application.SliceResponse;
import com.jisungin.application.review.response.QReviewContentResponse;
import com.jisungin.application.review.response.QReviewWithRatingResponse;
import com.jisungin.application.review.response.ReviewContentResponse;
import com.jisungin.application.review.response.ReviewWithRatingResponse;
import com.jisungin.domain.review.RatingOrderType;
import com.jisungin.domain.review.ReviewOrderType;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static com.jisungin.domain.book.QBook.*;
import static com.jisungin.domain.rating.QRating.*;
import static com.jisungin.domain.review.QReview.review;
import static com.jisungin.domain.review.RatingOrderType.*;
import static com.jisungin.domain.reviewlike.QReviewLike.*;
import static com.jisungin.domain.user.QUser.*;

@Slf4j
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

    private final JPAQueryFactory queryFactory;

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

    @Override
    public SliceResponse<ReviewWithRatingResponse> findAllByBookId(String isbn, Integer offset, Integer limit,
                                                                   String order) {
        ReviewOrderType orderType = ReviewOrderType.fromString(order);

        JPAQuery<ReviewWithRatingResponse> query = queryFactory.
                select(new QReviewWithRatingResponse(
                        review.id.as("reviewId"),
                        rating1.id.as("ratingId"),
                        user.name.as("username"),
                        user.profileImage.as("profileImage"),
                        review.content.as("reviewContent"),
                        rating1.rating.as("starRating"),
                        reviewLike.id.count().as("likeCount")
                ))
                .from(review)
                .join(review.user, user)
                .join(review.book, book)
                .leftJoin(reviewLike).on(review.eq(reviewLike.review))
                .groupBy(review.id)
                .orderBy(orderType.getOrderSpecifier())
                .offset(offset)
                .limit(limit + 1);

        orderType.applyJoinStrategy(query);

        List<ReviewWithRatingResponse> content = query.fetch();

        return SliceResponse.of(content, offset, limit, hasNextPage(content, limit));
    }

    private List<ReviewContentResponse> getReviewContents(
            Long userId, RatingOrderType orderType, int size, int offset) {
        return queryFactory
                .select(new QReviewContentResponse(
                        review.id, review.user.profileImage, review.user.name, rating1.rating, review.content,
                        review.book.isbn, review.book.title, review.book.imageUrl
                ))
                .from(review)
                .leftJoin(rating1).on(review.user.eq(rating1.user), review.book.eq(rating1.book))
                .where(review.user.id.eq(userId))
                .orderBy(createSpecifier(orderType), review.book.title.asc())
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

    // 만약 별점 필터링 조건이 존재하면 해당하는 별점만 가져온다.
    private BooleanExpression ratingCondition(Double rating) {
        if (rating == null) {
            return null;
        }
        return rating1.rating.eq(rating);
    }

    private <T> boolean hasNextPage(List<T> content, int limit) {
        boolean hasNext = content.size() > limit;

        if (hasNext) {
            content.remove(limit);
        }

        return hasNext;
    }

}
