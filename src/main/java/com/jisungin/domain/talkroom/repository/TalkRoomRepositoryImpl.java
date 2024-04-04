package com.jisungin.domain.talkroom.repository;

import static com.jisungin.domain.book.QBook.book;
import static com.jisungin.domain.comment.QComment.comment;
import static com.jisungin.domain.commentlike.QCommentLike.commentLike;
import static com.jisungin.domain.talkroom.QTalkRoom.talkRoom;
import static com.jisungin.domain.talkroom.QTalkRoomRole.talkRoomRole;
import static com.jisungin.domain.talkroom.repository.OrderType.RECENT;
import static com.jisungin.domain.talkroom.repository.OrderType.RECOMMEND;
import static com.jisungin.domain.talkroomlike.QTalkRoomLike.talkRoomLike;
import static com.jisungin.domain.user.QUser.user;

import com.jisungin.application.PageResponse;
import com.jisungin.application.comment.response.CommentLikeUserIdResponse;
import com.jisungin.application.comment.response.CommentQueryResponse;
import com.jisungin.application.comment.response.QCommentLikeUserIdResponse;
import com.jisungin.application.comment.response.QCommentQueryResponse;
import com.jisungin.application.talkroom.response.QTalkRoomFindAllResponse;
import com.jisungin.application.talkroom.response.QTalkRoomFindOneResponse;
import com.jisungin.application.talkroom.response.QTalkRoomLikeUserIdResponse;
import com.jisungin.application.talkroom.response.QTalkRoomQueryReadingStatusResponse;
import com.jisungin.application.talkroom.response.TalkRoomFindAllResponse;
import com.jisungin.application.talkroom.response.TalkRoomFindOneResponse;
import com.jisungin.application.talkroom.response.TalkRoomLikeUserIdResponse;
import com.jisungin.application.talkroom.response.TalkRoomQueryReadingStatusResponse;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TalkRoomRepositoryImpl implements TalkRoomRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    // 토크룸 페이징 조회
    @Override
    public PageResponse<TalkRoomFindAllResponse> findAllTalkRoom(long offset, int size, String order, String query) {

        //루트 조회(toOne 코드를 모두 한번에 조회) -> Query 1번 발생
        List<TalkRoomFindAllResponse> findTalkRoom = findTalkRoomBySearch(offset, size, order, query);

        //TalkRoomRole 컬렉션을 MAP 한방에 조회 -> Query 1번 발생
        Map<Long, List<TalkRoomQueryReadingStatusResponse>> talkRoomRoleMap = findTalkRoomRoleMap(
                toTalkRoomIds(findTalkRoom));

        //루프를 돌면서 컬렉션 추가(추가 쿼리 실행X)
        findTalkRoom.forEach(t -> t.addTalkRoomStatus(talkRoomRoleMap.get(t.getTalkRoomId())));

        // 좋아요한 유저 ID 정보들 추가 -> Query 1번 발생
        Map<Long, List<TalkRoomLikeUserIdResponse>> talkRoomLikeUserMap = findAllTalkRoomLikeUserId(
                toTalkRoomIds(findTalkRoom));
        findTalkRoom.forEach(t -> t.addTalkRoomLikeUserIds(talkRoomLikeUserMap.get(t.getTalkRoomId())));

        // query 1번 발생
        long totalCount = getTotalTalkRoomCount();

        return PageResponse.<TalkRoomFindAllResponse>builder()
                .queryResponse(findTalkRoom)
                .totalCount(totalCount)
                .size(size)
                .build();
    }

    // 토크룸 단건 조회
    @Override
    public TalkRoomFindOneResponse findOneTalkRoom(Long talkRoomId) {
        TalkRoomFindOneResponse findOneTalkRoom = findTalkRoomByTalkRoomId(talkRoomId);

        List<TalkRoomQueryReadingStatusResponse> talkRoomRoles = findTalkRoomRoleByTalkRoomId(talkRoomId);
        findOneTalkRoom.addTalkRoomStatus(talkRoomRoles);

        List<CommentQueryResponse> talkRoomComments = findCommentsByTalkRoomId(talkRoomId);
        findOneTalkRoom.addTalkRoomComments(talkRoomComments);

        List<Long> commentIds = talkRoomComments.stream()
                .map(CommentQueryResponse::getCommentId)
                .collect(Collectors.toList());

        List<TalkRoomLikeUserIdResponse> userIds = findOneTalkRoomLikeUserId(talkRoomId);
        findOneTalkRoom.addUserIds(userIds);

        Map<Long, List<CommentLikeUserIdResponse>> CommentLikeUserIdMap = findAllCommentLikeUserId(commentIds);
        talkRoomComments.forEach(c -> c.addLikeUserIds(CommentLikeUserIdMap.get(c.getCommentId())));

        return findOneTalkRoom;
    }

    /**
     * 유저 ID로 유저가 생성한 토론방 찾아오는 메서드
     */
    public void findAllUserCreatedTalkRooms(Long userId) {
        List<TalkRoomFindAllResponse> userTalkRooms = findAllUserTalkRooms(userId);

        Map<Long, List<TalkRoomLikeUserIdResponse>> talkRoomLikeUserMap = findAllTalkRoomLikeUserId(
                toTalkRoomIds(userTalkRooms));
        userTalkRooms.forEach(t -> t.addTalkRoomLikeUserIds(talkRoomLikeUserMap.get(t.getTalkRoomId())));
    }

    private List<TalkRoomFindAllResponse> findAllUserTalkRooms(Long userId) {
        return queryFactory.select(new QTalkRoomFindAllResponse(
                        talkRoom.id.as("talkRoomId"),
                        user.name.as("userName"),
                        talkRoom.title,
                        talkRoom.content,
                        book.title,
                        book.imageUrl,
                        talkRoomLike.count().as("likeCount")
                ))
                .from(talkRoom)
                .join(talkRoom.user, user)
                .join(talkRoom.book, book)
                .leftJoin(talkRoomLike).on(talkRoom.eq(talkRoomLike.talkRoom))
                .groupBy(talkRoom.id)
                .where(talkRoom.user.id.eq(userId))
                .fetch();
    }

    // 토크룸 페이징 조회 쿼리
    private List<TalkRoomFindAllResponse> findTalkRoomBySearch(long offset, int size, String order, String query) {
        return queryFactory.select(new QTalkRoomFindAllResponse(
                        talkRoom.id.as("talkRoomId"),
                        user.name.as("userName"),
                        talkRoom.title,
                        talkRoom.content,
                        book.title,
                        book.imageUrl.as("bookImage"),
                        talkRoomLike.count().as("likeCount")
                ))
                .from(talkRoom)
                .join(talkRoom.user, user)
                .join(talkRoom.book, book)
                .leftJoin(talkRoomLike).on(talkRoom.eq(talkRoomLike.talkRoom))
                .groupBy(talkRoom.id)
                .where(searchQuery(query))
                .offset(offset)
                .limit(size)
                .orderBy(condition(OrderType.convertToOrderType(order)))
                .fetch();
    }

    private BooleanExpression searchQuery(String search) {
        return search != null ? talkRoom.title.contains(search) : null;
    }

    // 쿼리에서 가져온 토크룸 ID를 List<Long> 객체에 넣어주는 로직
    private List<Long> toTalkRoomIds(List<TalkRoomFindAllResponse> findTalkRoom) {
        return findTalkRoom.stream()
                .map(t -> t.getTalkRoomId())
                .collect(Collectors.toList());
    }

    // 토크룸 상태 가져온 후 Map<>에 넣어주는 로직
    private Map<Long, List<TalkRoomQueryReadingStatusResponse>> findTalkRoomRoleMap(List<Long> talkRoomIds) {
        List<TalkRoomQueryReadingStatusResponse> talkRoomRoles = queryFactory.select(
                        new QTalkRoomQueryReadingStatusResponse(
                                talkRoom.id,
                                talkRoomRole.readingStatus
                        ))
                .from(talkRoomRole)
                .join(talkRoomRole.talkRoom, talkRoom)
                .where(talkRoomRole.talkRoom.id.in(talkRoomIds))
                .fetch();

        return talkRoomRoles.stream()
                .collect(Collectors.groupingBy(TalkRoomQueryReadingStatusResponse::getTalkRoomId));
    }

    // 페이징 조회 -> 토크룸 좋아요 누른 사용자 ID 가져온 후 Map<>에 넣어주는 로직
    private Map<Long, List<TalkRoomLikeUserIdResponse>> findAllTalkRoomLikeUserId(List<Long> talkRoomIds) {
        List<TalkRoomLikeUserIdResponse> talkRoomLikeUserIds = queryFactory.select(new QTalkRoomLikeUserIdResponse(
                        talkRoom.id.as("talkRoomId"),
                        user.id.as("userId")
                ))
                .from(talkRoomLike)
                .join(talkRoomLike.talkRoom, talkRoom)
                .join(talkRoomLike.user, user)
                .where(talkRoomLike.talkRoom.id.in(talkRoomIds))
                .fetch();

        return talkRoomLikeUserIds.stream()
                .collect(Collectors.groupingBy(TalkRoomLikeUserIdResponse::getTalkRoomId));
    }

    // 단건 조회 -> 토크룸 좋아요 누른 사용자 ID 가져온 후 Map<>에 넣어주는 로직
    private List<TalkRoomLikeUserIdResponse> findOneTalkRoomLikeUserId(Long talkRoomId) {
        return queryFactory.select(new QTalkRoomLikeUserIdResponse(
                        talkRoom.id.as("talkRoomId"),
                        user.id.as("userId")
                ))
                .from(talkRoomLike)
                .join(talkRoomLike.talkRoom, talkRoom)
                .join(talkRoomLike.user, user)
                .where(talkRoomLike.talkRoom.id.eq(talkRoomId))
                .fetch();
    }

    // 토크룸 단건 조회 시 토크룸 상태 가져오는 쿼리
    private List<TalkRoomQueryReadingStatusResponse> findTalkRoomRoleByTalkRoomId(Long talkRoomId) {
        return queryFactory.select(new QTalkRoomQueryReadingStatusResponse(
                        talkRoom.id,
                        talkRoomRole.readingStatus
                ))
                .from(talkRoomRole)
                .join(talkRoomRole.talkRoom, talkRoom)
                .where(talkRoomRole.talkRoom.id.eq(talkRoomId))
                .fetch();
    }

    // 토크룸에 저장된 의견들 가져오는 쿼리
    private List<CommentQueryResponse> findCommentsByTalkRoomId(Long talkRoomId) {
        return queryFactory.select(new QCommentQueryResponse(
                        comment.id.as("commentId"),
                        user.name.as("userName"),
                        comment.content,
                        commentLike.count().as("commentLikeCount")
                ))
                .from(comment)
                .join(comment.talkRoom, talkRoom)
                .join(comment.user, user)
                .leftJoin(commentLike).on(comment.eq(commentLike.comment))
                .groupBy(comment.id)
                .where(comment.talkRoom.id.eq(talkRoomId))
                .fetch();
    }

    private Map<Long, List<CommentLikeUserIdResponse>> findAllCommentLikeUserId(List<Long> commentIds) {
        List<CommentLikeUserIdResponse> commentLikeUserIds = queryFactory.select(new QCommentLikeUserIdResponse(
                        comment.id.as("commentId"),
                        user.id.as("userId")
                ))
                .from(commentLike)
                .join(commentLike.comment, comment)
                .join(commentLike.user, user)
                .where(commentLike.comment.id.in(commentIds))
                .fetch();

        return commentLikeUserIds.stream()
                .collect(Collectors.groupingBy(CommentLikeUserIdResponse::getCommentId));
    }

    // 토크룸 전체 개수 가져오는 쿼리
    private long getTotalTalkRoomCount() {
        return queryFactory
                .select(talkRoom.count())
                .from(talkRoom)
                .join(talkRoom.user, user)
                .join(talkRoom.book, book)
                .fetchOne();
    }

    // 토크룸 단건 조회 쿼리
    private TalkRoomFindOneResponse findTalkRoomByTalkRoomId(Long talkRoomId) {
        return queryFactory.select(new QTalkRoomFindOneResponse(
                        talkRoom.id.as("talkRoomId"),
                        user.name.as("userName"),
                        talkRoom.title,
                        talkRoom.content,
                        book.title,
                        book.imageUrl.as("bookImage"),
                        talkRoomLike.count().as("likeCount"),
                        comment.count().as("commentCount")
                ))
                .from(talkRoom)
                .join(talkRoom.user, user)
                .join(talkRoom.book, book)
                .leftJoin(talkRoomLike).on(talkRoom.eq(talkRoomLike.talkRoom))
                .leftJoin(comment).on(talkRoom.eq(comment.talkRoom))
                .groupBy(talkRoom.id)
                .where(talkRoom.id.eq(talkRoomId))
                .fetchOne();
    }

    private OrderSpecifier<?> condition(OrderType orderType) {
        if (RECENT.equals(orderType)) {
            return talkRoom.createDateTime.desc();
        } else if (RECOMMEND.equals(orderType)) {
            return talkRoomLike.count().desc();
        }
        return OrderByNull.DEFAULT;
    }

}
