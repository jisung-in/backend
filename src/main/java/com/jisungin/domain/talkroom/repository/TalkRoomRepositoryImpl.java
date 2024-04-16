package com.jisungin.domain.talkroom.repository;

import static com.jisungin.domain.book.QBook.book;
import static com.jisungin.domain.comment.QComment.comment;
import static com.jisungin.domain.talkroom.QTalkRoom.talkRoom;
import static com.jisungin.domain.talkroom.repository.OrderType.RECENT;
import static com.jisungin.domain.talkroom.repository.OrderType.RECENT_COMMENT;
import static com.jisungin.domain.talkroom.repository.OrderType.RECOMMEND;
import static com.jisungin.domain.talkroomlike.QTalkRoomLike.talkRoomLike;
import static com.jisungin.domain.user.QUser.user;

import com.jisungin.application.talkroom.response.QTalkRoomQueryResponse;
import com.jisungin.application.talkroom.response.TalkRoomQueryResponse;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TalkRoomRepositoryImpl implements TalkRoomRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<TalkRoomQueryResponse> findAllTalkRoom(long offset, int size, String order, String query) {
        return findTalkRoomBySearch(offset, size, order, query);
    }

    @Override
    public Long countTalkRooms() {
        return queryFactory
                .select(talkRoom.count())
                .from(talkRoom)
                .join(talkRoom.user, user)
                .join(talkRoom.book, book)
                .fetchOne();
    }

    // 책과 연관된 TalkRoomResponse 조회
    @Override
    public List<TalkRoomQueryResponse> findTalkRoomsRelatedBook(String isbn, long offset, Integer size) {
        return queryFactory.select(new QTalkRoomQueryResponse(
                        talkRoom.id,
                        user.profileImage,
                        user.name.as("username"),
                        talkRoom.title,
                        talkRoom.content,
                        book.title.as("bookName"),
                        book.thumbnail.as("bookThumbnail"),
                        talkRoomLike.count().as("likeCount"),
                        talkRoom.createDateTime.as("createTime")
                ))
                .from(talkRoom)
                .join(talkRoom.user, user)
                .join(talkRoom.book, book)
                .leftJoin(talkRoomLike).on(talkRoom.eq(talkRoomLike.talkRoom))
                .where(book.isbn.eq(isbn))
                .groupBy(talkRoom.id)
                .offset(offset)
                .limit(size)
                .orderBy(talkRoomLike.count().desc())
                .fetch();
    }

    public Long countTalkRoomsRelatedBook(String isbn) {
        return queryFactory.select(talkRoom.count())
                .from(talkRoom)
                .join(talkRoom.book, book)
                .where(book.isbn.eq(isbn))
                .fetchOne();
    }

    // 토크룸 단건 조회
    @Override
    public TalkRoomQueryResponse findOneTalkRoom(Long talkRoomId) {
        return findTalkRoomByTalkRoomId(talkRoomId);
    }

    // 토크룸 페이징 조회 쿼리
    private List<TalkRoomQueryResponse> findTalkRoomBySearch(long offset, int size, String order, String query) {
        JPAQuery<TalkRoomQueryResponse> jpaQuery = queryFactory.select(new QTalkRoomQueryResponse(
                        talkRoom.id.as("talkRoomId"),
                        user.profileImage,
                        user.name.as("userName"),
                        talkRoom.title,
                        talkRoom.content,
                        book.title,
                        book.thumbnail.as("bookThumbnail"),
                        talkRoomLike.count().as("likeCount"),
                        talkRoom.createDateTime.as("createTime")
                ))
                .from(talkRoom)
                .join(talkRoom.user, user)
                .join(talkRoom.book, book)
                .leftJoin(talkRoomLike).on(talkRoom.eq(talkRoomLike.talkRoom))
                .from(talkRoom);

        addJoinByOrder(jpaQuery, OrderType.convertToOrderType(order));

        List<TalkRoomQueryResponse> response = jpaQuery.groupBy(talkRoom.id)
                .where(searchQuery(query))
                .offset(offset)
                .limit(size)
                .orderBy(condition(OrderType.convertToOrderType(order)))
                .fetch();

        return response;
    }

    private void addJoinByOrder(JPAQuery<?> jpaQuery, OrderType orderType) {
        if (RECENT_COMMENT.equals(orderType)) {
            jpaQuery
                    .leftJoin(comment).on(talkRoom.eq(comment.talkRoom));
        }
    }

    private BooleanExpression searchQuery(String search) {
        return search != null ? talkRoom.title.contains(search) : null;
    }

    // 토크룸 단건 조회 쿼리
    private TalkRoomQueryResponse findTalkRoomByTalkRoomId(Long talkRoomId) {
        return queryFactory.select(new QTalkRoomQueryResponse(
                        talkRoom.id.as("id"),
                        user.profileImage,
                        user.name.as("username"),
                        talkRoom.title,
                        talkRoom.content,
                        book.title,
                        book.thumbnail.as("bookImage"),
                        talkRoomLike.count().as("likeCount"),
                        talkRoom.createDateTime.as("createTime")
                ))
                .from(talkRoom)
                .join(talkRoom.user, user)
                .join(talkRoom.book, book)
                .leftJoin(talkRoomLike).on(talkRoom.eq(talkRoomLike.talkRoom))
                .where(talkRoom.id.eq(talkRoomId))
                .fetchOne();
    }

    private OrderSpecifier<?> condition(OrderType orderType) {
        if (RECENT.equals(orderType)) {
            return talkRoom.createDateTime.desc();
        } else if (RECOMMEND.equals(orderType)) {
            return talkRoomLike.count().desc();
        } else if (RECENT_COMMENT.equals(orderType)) {
            return comment.createDateTime.max().desc();
        }
        return OrderByNull.DEFAULT;
    }

}
