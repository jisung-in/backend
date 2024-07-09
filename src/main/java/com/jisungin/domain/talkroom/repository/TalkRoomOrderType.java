package com.jisungin.domain.talkroom.repository;

import static com.jisungin.domain.talkroom.QTalkRoom.talkRoom;
import static com.jisungin.domain.talkroomlike.QTalkRoomLike.talkRoomLike;

import com.querydsl.core.types.OrderSpecifier;
import java.util.function.Supplier;

public enum TalkRoomOrderType {

    DEFAULT(() -> OrderByNull.DEFAULT),
    RECENT(talkRoom.createDateTime::desc),
    RECOMMEND(() -> talkRoomLike.count().desc());

    private final Supplier<OrderSpecifier<?>> orderSpecifierSupplier;

    TalkRoomOrderType(Supplier<OrderSpecifier<?>> orderSpecifierSupplier) {
        this.orderSpecifierSupplier = orderSpecifierSupplier;
    }

    public static OrderSpecifier<?> getOrderSpecifierByName(String name) {
        try {
            return TalkRoomOrderType.valueOf(name.toUpperCase()).getOrderSpecifier();
        } catch (IllegalArgumentException | NullPointerException e) {
            return TalkRoomOrderType.DEFAULT.getOrderSpecifier();
        }
    }

    private OrderSpecifier<?> getOrderSpecifier() {
        return orderSpecifierSupplier.get();
    }

}
