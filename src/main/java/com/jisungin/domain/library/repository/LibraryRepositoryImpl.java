package com.jisungin.domain.library.repository;

import static com.jisungin.domain.book.QBook.book;
import static com.jisungin.domain.library.QLibrary.library;
import static com.jisungin.domain.rating.QRating.rating1;
import static com.jisungin.domain.user.QUser.user;

import com.jisungin.application.PageResponse;
import com.jisungin.application.library.response.QUserReadingStatusResponse;
import com.jisungin.application.library.response.UserReadingStatusResponse;
import com.jisungin.domain.ReadingStatus;
import com.jisungin.domain.library.LibraryQueryEntity;
import com.jisungin.domain.library.QLibraryQueryEntity;
import com.jisungin.domain.library.ReadingStatusOrderType;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class LibraryRepositoryImpl implements LibraryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<LibraryQueryEntity> findAllByUserId(Long userId) {
        return queryFactory.select(new QLibraryQueryEntity(
                        library.id.as("id"),
                        book.isbn.as("bookIsbn"),
                        library.status.as("readingStatus")
                ))
                .from(library)
                .join(library.book, book)
                .join(library.user, user)
                .where(library.user.id.eq(userId))
                .fetch();
    }

    @Override
    public PageResponse<UserReadingStatusResponse> findAllReadingStatusOrderBy(
            Long userId, ReadingStatus readingStatus, ReadingStatusOrderType orderType, int size, int offset) {
        log.info("--------------start--------------");
        List<UserReadingStatusResponse> userReadingStatuses = queryFactory
                .select(new QUserReadingStatusResponse(
                        library.book.isbn, library.book.imageUrl, library.book.title, rating1.rating.avg()))
                .from(library)
                .join(rating1).on(library.book.eq(rating1.book))
                .where(library.user.id.eq(userId), library.status.eq(readingStatus))
                .groupBy(library.book.isbn)
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
                .from(library)
                .join(library.book, book)
                .join(library.user, user)
                .where(library.user.id.eq(userId)
                        .and(library.book.isbn.eq(bookIsbn)))
                .fetchFirst();

        return fetchOne != null;
    }

    private long getTotalCount(Long userId, ReadingStatus readingStatus) {
        return queryFactory
                .select(library.count())
                .from(library)
                .where(library.user.id.eq(userId), library.status.eq(readingStatus))
                .fetchOne();
    }

    private OrderSpecifier createSpecifier(ReadingStatusOrderType orderType) {
        if (orderType.equals(ReadingStatusOrderType.RATING_AVG_DESC)) {
            return rating1.rating.avg().desc();
        }

        return library.book.title.asc();
    }

}
