package com.jisungin.domain.book.repository;

import static com.jisungin.domain.book.QBook.book;

import com.jisungin.application.book.response.BookFindAllResponse;
import com.jisungin.application.book.response.QBookFindAllResponse;
import com.jisungin.domain.book.BookOrderType;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BookRepositoryImpl implements BookRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<BookFindAllResponse> getBooks(Integer offset, Integer size, String order) {
        BookOrderType orderType = BookOrderType.fromString(order);

        JPAQuery<BookFindAllResponse> query = selectFromBookFindAllResponse();

        orderType.applyJoinStrategy(query);
        orderType.applyGroupStrategy(query);

        return query
                .offset(offset)
                .limit(size)
                .orderBy(orderType.getOrderSpecifier())
                .fetch();
    }

    @Override
    public Long getTotalCount(String order) {
        BookOrderType orderType = BookOrderType.fromString(order);

        JPAQuery<Long> query = queryFactory
                .select(book.countDistinct())
                .from(book);

        orderType.applyJoinStrategy(query);

        return query.fetchOne();
    }

    private JPAQuery<BookFindAllResponse> selectFromBookFindAllResponse() {
        return queryFactory.select(new QBookFindAllResponse(
                        book.isbn,
                        book.title,
                        book.publisher,
                        book.thumbnail,
                        book.authors,
                        book.dateTime
                ))
                .from(book);
    }

}
