package com.jisungin.domain.talkroom.repository;

import static com.jisungin.domain.book.QBook.book;
import static com.jisungin.domain.comment.QComment.comment;
import static com.jisungin.domain.talkroom.QTalkRoom.talkRoom;
import static com.jisungin.domain.talkroomlike.QTalkRoomLike.talkRoomLike;
import static com.jisungin.domain.user.QUser.user;

import com.jisungin.application.talkroom.response.QTalkRoomQueryEntity;
import com.jisungin.application.talkroom.response.TalkRoomQueryEntity;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TalkRoomRepositoryImpl implements TalkRoomRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    // 토크방 단건 조회
    @Override
    public TalkRoomQueryEntity findOneTalkRoom(Long talkRoomId) {
        return selectFromTalkRoomQueryEntity()
                .where(talkRoom.id.eq(talkRoomId))
                .fetchOne();
    }

    // 토크방 페이징 조회
    @Override
    public List<TalkRoomQueryEntity> findAllTalkRoom(Integer offset, Integer size, String order, String search,
                                                     String day, LocalDateTime now
    ) {
        return selectFromTalkRoomQueryEntity()
                .where(searchQuery(search), dataTimeEq(OrderDay.of(day), now))
                .offset(offset)
                .limit(size)
                .groupBy(talkRoom.id)
                .orderBy(TalkRoomOrderType.getOrderSpecifierByName(order))
                .fetch();
    }

    // 토크방 페이징 개수 조회
    @Override
    public Long countTalkRooms(String search, String day, LocalDateTime now) {
        return queryFactory
                .select(talkRoom.count())
                .from(talkRoom)
                .join(talkRoom.user, user)
                .join(talkRoom.book, book)
                .where(searchQuery(search), dataTimeEq(OrderDay.of(day), now))
                .fetchOne();
    }

    // 도서와 연관된 토크룸 페이징 조회
    @Override
    public List<TalkRoomQueryEntity> findTalkRoomsRelatedBook(String isbn, long offset, Integer size) {
        return selectFromTalkRoomQueryEntity()
                .where(book.isbn.eq(isbn))
                .groupBy(talkRoom.id)
                .offset(offset)
                .limit(size)
                .orderBy(talkRoomLike.count().desc())
                .fetch();
    }

    // 도서와 연관된 토크룸 페이징 개수 조회
    public Long countTalkRoomsRelatedBook(String isbn) {
        return queryFactory.select(talkRoom.count())
                .from(talkRoom)
                .join(talkRoom.book, book)
                .where(book.isbn.eq(isbn))
                .fetchOne();
    }

    // 사용자 토크붕 페이징 조회
    @Override
    public List<TalkRoomQueryEntity> findByTalkRoomOwner(Integer offset, Integer size, boolean userTalkRoomsFilter,
                                                         boolean commentFilter, boolean likeFilter, Long userId
    ) {
        return selectFromTalkRoomQueryEntity()
                .leftJoin(comment).on(talkRoom.eq(comment.talkRoom))
                .where(userTalkRoomEq(userTalkRoomsFilter, userId), commentEq(commentFilter, userId),
                        likeEq(likeFilter, userId))
                .groupBy(talkRoom.id)
                .offset(offset)
                .limit(size)
                .orderBy(talkRoom.registeredDateTime.desc())
                .fetch();
    }

    // 사용자 토크방 페이징 개수 조회
    @Override
    public Long countTalkRoomsByUserId(Long userId, boolean userTalkRoomsFilter, boolean commentFilter,
                                       boolean likeFilter) {
        return queryFactory
                .select(talkRoom.count())
                .from(talkRoom)
                .join(talkRoom.book, book)
                .leftJoin(talkRoomLike).on(talkRoom.eq(talkRoomLike.talkRoom))
                .leftJoin(comment).on(talkRoom.eq(comment.talkRoom))
                .join(talkRoom.user, user)
                .where(userTalkRoomEq(userTalkRoomsFilter, userId), commentEq(commentFilter, userId),
                        likeEq(likeFilter, userId))
                .fetchOne();
    }

    private JPAQuery<TalkRoomQueryEntity> selectFromTalkRoomQueryEntity() {
        return queryFactory.select(new QTalkRoomQueryEntity(
                        talkRoom.id.as("talkRoomId"),
                        user.profileImage,
                        user.name.as("userName"),
                        talkRoom.title,
                        talkRoom.content,
                        book.isbn.as("bookIsbn"),
                        book.title,
                        book.authors.as("bookAuthor"),
                        book.thumbnail.as("bookThumbnail"),
                        talkRoomLike.count().as("likeCount"),
                        talkRoom.registeredDateTime.as("registeredDateTime"),
                        user.id.as("creatorId")

                ))
                .from(talkRoom)
                .join(talkRoom.user, user)
                .join(talkRoom.book, book)
                .leftJoin(talkRoomLike).on(talkRoom.eq(talkRoomLike.talkRoom));
    }

    private BooleanExpression userTalkRoomEq(boolean userTalkRoomsFilter, Long userId) {
        return userTalkRoomsFilter ? talkRoom.user.id.eq(userId) : null;
    }

    private BooleanExpression commentEq(boolean commentFilter, Long userId) {
        return commentFilter ? comment.user.id.eq(userId) : null;
    }

    private BooleanExpression likeEq(boolean likeFilter, Long userId) {
        return likeFilter ? talkRoomLike.user.id.eq(userId) : null;
    }

    private BooleanExpression dataTimeEq(OrderDay orderDay, LocalDateTime now) {
        return orderDay != null ? talkRoom.registeredDateTime.goe(orderDay.getDataTime(now))
                .and(talkRoom.registeredDateTime.loe(now)) : null;
    }

    private BooleanExpression searchQuery(String search) {
        return search != null ? talkRoom.title.contains(search) : null;
    }

}
