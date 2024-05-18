package com.jisungin.domain.review;

import static com.jisungin.domain.rating.QRating.rating1;
import static com.jisungin.domain.review.QReview.review;
import static com.jisungin.domain.reviewlike.QReviewLike.reviewLike;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import java.util.function.Consumer;
import java.util.function.Supplier;

public enum ReviewOrderType {

    LIKE(() -> reviewLike.id.count().desc(), ReviewOrderType::leftJoinRating),
    RECENT(review.createDateTime::desc, ReviewOrderType::leftJoinRating),
    RATING_DESC(rating1.rating::desc, ReviewOrderType::joinRating),
    RATING_ASC(rating1.rating::asc, ReviewOrderType::joinRating);

    private final Supplier<OrderSpecifier<?>> orderSpecifierSupplier;
    private final Consumer<JPAQuery<?>> joinRatingStrategy;

    ReviewOrderType(Supplier<OrderSpecifier<?>> orderSpecifierSupplier, Consumer<JPAQuery<?>> joinRatingStrategy) {
        this.orderSpecifierSupplier = orderSpecifierSupplier;
        this.joinRatingStrategy = joinRatingStrategy;
    }

    public OrderSpecifier<?> getOrderSpecifier() {
        return orderSpecifierSupplier.get();
    }

    public void applyJoinStrategy(JPAQuery<?> query) {
        joinRatingStrategy.accept(query);
        applyCommonJoinConditions(query);
    }

    public static ReviewOrderType fromString(String name) {
        try {
            return ReviewOrderType.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            return ReviewOrderType.LIKE;
        }
    }

    private static void joinRating(JPAQuery<?> query) {
        query.join(rating1);
    }

    private static void leftJoinRating(JPAQuery<?> query) {
        query.leftJoin(rating1);
    }

    private static void applyCommonJoinConditions(JPAQuery<?> query) {
        query.on(review.user.eq(rating1.user)
                .and(review.book.eq(rating1.book)));
    }

}
