package com.jisungin.domain.talkroom.repository;

import static com.jisungin.application.OrderType.RECENT;
import static com.jisungin.application.OrderType.RECOMMEND;
import static com.jisungin.domain.book.QBook.book;
import static com.jisungin.domain.comment.QComment.comment;
import static com.jisungin.domain.talkroom.QTalkRoom.talkRoom;
import static com.jisungin.domain.talkroom.QTalkRoomRole.talkRoomRole;
import static com.jisungin.domain.talkroomlike.QTalkRoomLike.talkRoomLike;
import static com.jisungin.domain.user.QUser.user;

import com.jisungin.application.OrderType;
import com.jisungin.application.PageResponse;
import com.jisungin.application.talkroom.request.TalkRoomSearchServiceRequest;
import com.jisungin.application.talkroom.response.QTalkRoomFindAllResponse;
import com.jisungin.application.talkroom.response.QTalkRoomFindOneResponse;
import com.jisungin.application.talkroom.response.QTalkRoomLikeUserIdResponse;
import com.jisungin.application.talkroom.response.QTalkRoomQueryCommentsResponse;
import com.jisungin.application.talkroom.response.QTalkRoomQueryReadingStatusResponse;
import com.jisungin.application.talkroom.response.TalkRoomFindAllResponse;
import com.jisungin.application.talkroom.response.TalkRoomFindOneResponse;
import com.jisungin.application.talkroom.response.TalkRoomLikeUserIdResponse;
import com.jisungin.application.talkroom.response.TalkRoomQueryCommentsResponse;
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
    public PageResponse<TalkRoomFindAllResponse> findAllTalkRoom(TalkRoomSearchServiceRequest search) {

        //루트 조회(toOne 코드를 모두 한번에 조회) -> Query 1번 발생
        List<TalkRoomFindAllResponse> findTalkRoom = findTalkRoomBySearch(search);

        //TalkRoomRole 컬렉션을 MAP 한방에 조회 -> Query 1번 발생
        Map<Long, List<TalkRoomQueryReadingStatusResponse>> talkRoomRoleMap = findTalkRoomRoleMap(
                toTalkRoomIds(findTalkRoom));

        //루프를 돌면서 컬렉션 추가(추가 쿼리 실행X)
        findTalkRoom.forEach(t -> t.addTalkRoomStatus(talkRoomRoleMap.get(t.getTalkRoomId())));

        // 좋아요한 유저 ID 정보들 추가 -> Query 1번 발생
        Map<Long, List<TalkRoomLikeUserIdResponse>> talkRoomLikeUserMap = findTalkRoomLikeUserId(
                toTalkRoomIds(findTalkRoom));
        findTalkRoom.forEach(t -> t.addTalkRoomLikeUserIds(talkRoomLikeUserMap.get(t.getTalkRoomId())));

        // query 1번 발생
        long totalCount = getTotalTalkRoomCount();

        return PageResponse.<TalkRoomFindAllResponse>builder()
                .queryResponse(findTalkRoom)
                .totalCount(totalCount)
                .size(search.getSize())
                .build();
    }

    // 토크룸 단건 조회
    @Override
    public TalkRoomFindOneResponse findOneTalkRoom(Long talkRoomId) {
        TalkRoomFindOneResponse findOneTalkRoom = findTalkRoomByTalkRoomId(talkRoomId);

        List<TalkRoomQueryReadingStatusResponse> talkRoomRoles = findTalkRoomRoleByTalkRoomId(talkRoomId);
        findOneTalkRoom.addTalkRoomStatus(talkRoomRoles);

        List<TalkRoomQueryCommentsResponse> talkRoomComments = findCommentsByTalkRoomId(talkRoomId);
        findOneTalkRoom.addTalkRoomComments(talkRoomComments);

        List<TalkRoomLikeUserIdResponse> userIds = findOneTalkRoomLikeUserId(talkRoomId);
        findOneTalkRoom.addUserIds(userIds);

        return findOneTalkRoom;
    }

    // 토크룸 페이징 조회 쿼리
    private List<TalkRoomFindAllResponse> findTalkRoomBySearch(TalkRoomSearchServiceRequest search) {
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
                .where(searchQuery(search.getSearch()))
                .offset(search.getOffset())
                .limit(search.getSize())
                .orderBy(condition(search.getOrderType()))
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
    private Map<Long, List<TalkRoomLikeUserIdResponse>> findTalkRoomLikeUserId(List<Long> talkRoomIds) {
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
    private List<TalkRoomQueryCommentsResponse> findCommentsByTalkRoomId(Long talkRoomId) {
        return queryFactory.select(new QTalkRoomQueryCommentsResponse(
                        comment.id.as("commentId"),
                        user.name.as("userName"),
                        comment.content
                ))
                .from(comment)
                .join(comment.talkRoom, talkRoom)
                .join(comment.user, user)
                .where(comment.talkRoom.id.eq(talkRoomId))
                .fetch();
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
            return talkRoom.id.desc();
        } else if (RECOMMEND.equals(orderType)) {
            return talkRoomLike.count().desc();
        }
        return OrderByNull.DEFAULT;
    }

}
