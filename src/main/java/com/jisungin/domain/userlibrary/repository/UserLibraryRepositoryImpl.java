package com.jisungin.domain.userlibrary.repository;

import com.jisungin.application.PageResponse;
import com.jisungin.application.userlibrary.response.QUserReadingStatusResponse;
import com.jisungin.application.userlibrary.response.UserReadingStatusResponse;
import com.jisungin.domain.ReadingStatus;
import com.jisungin.domain.userlibrary.ReadingStatusOrderType;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static com.jisungin.domain.book.QBook.book;
import static com.jisungin.domain.review.QReview.review;
import static com.jisungin.domain.userlibrary.QUserLibrary.userLibrary;

@Slf4j
@RequiredArgsConstructor
public class UserLibraryRepositoryImpl implements UserLibraryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public PageResponse<UserReadingStatusResponse> findAllReadingStatusOrderBy(
            Long userId, ReadingStatus readingStatus, ReadingStatusOrderType orderType, int size, int offset) {
        log.info("--------------start--------------");
        List<UserReadingStatusResponse> userReadingStatuses = queryFactory
                .select(new QUserReadingStatusResponse(book.imageUrl, book.title, review.rating.avg()))
                .from(userLibrary)
                .join(book).on(userLibrary.book.isbn.eq(book.isbn))
                .leftJoin(review).on(userLibrary.book.isbn.eq(review.book.isbn))
                .where(userLibrary.user.id.eq(userId), userLibrary.status.eq(readingStatus))
                .groupBy(book.isbn)
                .orderBy(createSpecifier(orderType))
                .offset(offset)
                .limit(size)
                .fetch();

        return PageResponse.<UserReadingStatusResponse>builder()
                .queryResponse(userReadingStatuses)
                .totalCount(getTotalCount(userId, readingStatus))
                .size(size)
                .build();

    }

    private long getTotalCount(Long userId, ReadingStatus readingStatus) {
        return queryFactory
                .select(userLibrary.count())
                .from(userLibrary)
                .where(userLibrary.user.id.eq(userId), userLibrary.status.eq(readingStatus))
                .fetchOne();
    }

    private OrderSpecifier createSpecifier(ReadingStatusOrderType orderType) {
        if (orderType.equals(ReadingStatusOrderType.RATING_AVG_DESC)) {
            return review.rating.avg().desc();
        }

        return userLibrary.book.title.asc();
    }

}
