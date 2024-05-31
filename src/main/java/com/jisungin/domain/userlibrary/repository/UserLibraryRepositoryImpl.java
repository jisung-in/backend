package com.jisungin.domain.userlibrary.repository;

import static com.jisungin.domain.book.QBook.book;
import static com.jisungin.domain.rating.QRating.*;
import static com.jisungin.domain.user.QUser.user;
import static com.jisungin.domain.userlibrary.QUserLibrary.userLibrary;

import com.jisungin.application.PageResponse;
import com.jisungin.application.userlibrary.response.QUserReadingStatusResponse;
import com.jisungin.application.userlibrary.response.UserReadingStatusResponse;
import com.jisungin.domain.ReadingStatus;
import com.jisungin.domain.userlibrary.ReadingStatusOrderType;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class UserLibraryRepositoryImpl implements UserLibraryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public PageResponse<UserReadingStatusResponse> findAllReadingStatusOrderBy(
            Long userId, ReadingStatus readingStatus, ReadingStatusOrderType orderType, int size, int offset) {
        log.info("--------------start--------------");
        List<UserReadingStatusResponse> userReadingStatuses = queryFactory
                .select(new QUserReadingStatusResponse(
                        userLibrary.book.isbn, userLibrary.book.imageUrl, userLibrary.book.title, rating1.rating.avg()))
                .from(userLibrary)
                .join(rating1).on(userLibrary.book.eq(rating1.book))
                .where(userLibrary.user.id.eq(userId), userLibrary.status.eq(readingStatus))
                .groupBy(userLibrary.book.isbn)
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

    @Override
    public Boolean existsByUserIdAndBookId(Long userId, String bookIsbn) {
        Integer fetchOne = queryFactory
                .selectOne()
                .from(userLibrary)
                .join(userLibrary.book, book)
                .join(userLibrary.user, user)
                .where(userLibrary.user.id.eq(userId)
                        .and(userLibrary.book.isbn.eq(bookIsbn)))
                .fetchFirst();

        return fetchOne != null;
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
            return rating1.rating.avg().desc();
        }

        return userLibrary.book.title.asc();
    }

}
