package com.jisungin.domain.book;

import static com.jisungin.domain.book.QBook.book;
import static com.jisungin.domain.comment.QComment.comment;
import static com.jisungin.domain.talkroom.QTalkRoom.talkRoom;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import java.util.function.Consumer;
import java.util.function.Supplier;

public enum BookOrderType {

    RECENT(book.createDateTime::desc, BookOrderType::noOption,BookOrderType::noOption),
    COMMENT(() -> comment.count().desc(), BookOrderType::joinWithComment, BookOrderType::groupByBook);

    private final Supplier<OrderSpecifier<?>> orderSpecifierSupplier;
    private final Consumer<JPAQuery<?>> joinStrategy;
    private final Consumer<JPAQuery<?>> groupStrategy;

    BookOrderType(Supplier<OrderSpecifier<?>> orderSpecifierSupplier, Consumer<JPAQuery<?>> joinStrategy,
                  Consumer<JPAQuery<?>> groupStrategy) {
        this.orderSpecifierSupplier = orderSpecifierSupplier;
        this.joinStrategy = joinStrategy;
        this.groupStrategy = groupStrategy;
    }

    public OrderSpecifier<?> getOrderSpecifier() {
        return orderSpecifierSupplier.get();
    }

    public void applyJoinStrategy(JPAQuery<?> query) {
        joinStrategy.accept(query);
    }

    public void applyGroupStrategy(JPAQuery<?> query) {
        groupStrategy.accept(query);
    }

    public static BookOrderType fromString(String name) {
        try {
            return BookOrderType.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            return BookOrderType.RECENT;
        }
    }

    private static void joinWithComment(JPAQuery<?> query) {
        query
                .join(talkRoom).on(talkRoom.book.isbn.eq(book.isbn))
                .join(comment).on(comment.talkRoom.id.eq(talkRoom.id));
    }

    private static void groupByBook(JPAQuery<?> query) {
        query.groupBy(book.isbn);
    }

    private static void noOption(JPAQuery<?> query) {
    }

}
