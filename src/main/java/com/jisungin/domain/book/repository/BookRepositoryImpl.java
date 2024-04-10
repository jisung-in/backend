package com.jisungin.domain.book.repository;

import static com.jisungin.domain.book.QBook.book;
import static com.jisungin.domain.comment.QComment.comment;
import static com.jisungin.domain.talkroom.QTalkRoom.talkRoom;
import static com.jisungin.domain.talkroom.repository.OrderType.COMMENT;
import static com.jisungin.domain.talkroom.repository.OrderType.RECENT;

import com.jisungin.application.PageResponse;
import com.jisungin.application.book.response.QSimpleBookResponse;
import com.jisungin.application.book.response.SimpleBookResponse;
import com.jisungin.domain.talkroom.repository.OrderByNull;
import com.jisungin.domain.talkroom.repository.OrderType;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BookRepositoryImpl implements BookRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public PageResponse<SimpleBookResponse> getBooks(Long offset, Integer size, String order) {
        JPAQuery<SimpleBookResponse> query = queryFactory.select(new QSimpleBookResponse(
                        book.isbn,
                        book.title,
                        book.publisher,
                        book.thumbnail,
                        book.authors,
                        book.dateTime
                ))
                .from(book);

        addJoinByOrder(query, OrderType.convertToOrderType(order));

        List<SimpleBookResponse> responses = query
                .offset(offset)
                .limit(size)
                .orderBy(condition(OrderType.convertToOrderType(order)))
                .fetch();


        Long totalCount = getTotalCount(OrderType.convertToOrderType(order));

        return PageResponse.of(size, totalCount, responses);
    }

    private Long getTotalCount(OrderType orderType) {
        JPAQuery<Long> countQuery = queryFactory
                .select(book.countDistinct())
                .from(book);

        addJoinByOrder(countQuery, orderType);

        return countQuery.fetchOne();
    }

    private void addJoinByOrder(JPAQuery<?> query, OrderType orderType) {
        if (COMMENT.equals(orderType)) {
            query
                    .join(talkRoom).on(talkRoom.book.isbn.eq(book.isbn))
                    .join(comment).on(talkRoom.id.eq(comment.talkRoom.id))
                    .groupBy(book.isbn);
        }
    }

    private OrderSpecifier<?> condition(OrderType orderType) {
        if (RECENT.equals(orderType)) {
            return book.createDateTime.desc();
        }
        if (COMMENT.equals(orderType)) {
            return comment.count().desc();
        }

        return OrderByNull.DEFAULT;
    }

}
