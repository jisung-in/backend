package com.jisungin.domain.talkroom.repository;

import static com.jisungin.domain.book.QBook.book;
import static com.jisungin.domain.comment.QComment.comment;
import static com.jisungin.domain.talkroom.QTalkRoom.talkRoom;
import static com.jisungin.domain.talkroom.QTalkRoomRole.talkRoomRole;
import static com.jisungin.domain.user.QUser.user;

import com.jisungin.application.response.PageResponse;
import com.jisungin.application.talkroom.request.TalkRoomSearchServiceRequest;
import com.jisungin.application.talkroom.response.QTalkRoomFindAllResponse;
import com.jisungin.application.talkroom.response.QTalkRoomFindOneResponse;
import com.jisungin.application.talkroom.response.QTalkRoomQueryComments;
import com.jisungin.application.talkroom.response.QTalkRoomQueryReadingStatus;
import com.jisungin.application.talkroom.response.TalkRoomFindAllResponse;
import com.jisungin.application.talkroom.response.TalkRoomFindOneResponse;
import com.jisungin.application.talkroom.response.TalkRoomQueryComments;
import com.jisungin.application.talkroom.response.TalkRoomQueryReadingStatus;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TalkRoomRepositoryImpl implements TalkRoomRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public PageResponse<TalkRoomFindAllResponse> findAllTalkRoom(TalkRoomSearchServiceRequest search) {

        //루트 조회(toOne 코드를 모두 한번에 조회)
        List<TalkRoomFindAllResponse> findTalkRoom = findTalkRoomBySearch(search);

        //TalkRoomRole 컬렉션을 MAP 한방에 조회
        Map<Long, List<TalkRoomQueryReadingStatus>> talkRoomRoleMap = findTalkRoomRoleMap(toTalkRoomIds(findTalkRoom));

        //루프를 돌면서 컬렉션 추가(추가 쿼리 실행X)
        findTalkRoom.forEach(t -> t.addTalkRoomStatus(talkRoomRoleMap.get(t.getTalkRoomId())));

        long totalCount = getTotalTalkRoomCount();

        return PageResponse.<TalkRoomFindAllResponse>builder()
                .queryResponse(findTalkRoom)
                .totalCount(totalCount)
                .size(search.getSize())
                .build();
    }

    @Override
    public TalkRoomFindOneResponse findOneTalkRoom(Long talkRoomId) {
        TalkRoomFindOneResponse findOneTalkRoom = findTalkRoomByTalkRoomId(talkRoomId);

        List<TalkRoomQueryReadingStatus> talkRoomRoles = findTalkRoomRoleByTalkRoomId(talkRoomId);
        findOneTalkRoom.addTalkRoomStatus(talkRoomRoles);

        List<TalkRoomQueryComments> talkRoomComments = findCommentsByTalkRoomId(talkRoomId);
        findOneTalkRoom.addTalkRoomComments(talkRoomComments);

        return findOneTalkRoom;
    }

    private List<TalkRoomQueryComments> findCommentsByTalkRoomId(Long talkRoomId) {
        return queryFactory.select(new QTalkRoomQueryComments(
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

    private List<TalkRoomQueryReadingStatus> findTalkRoomRoleByTalkRoomId(Long talkRoomId) {
        return queryFactory.select(new QTalkRoomQueryReadingStatus(
                        talkRoom.id,
                        talkRoomRole.readingStatus
                ))
                .from(talkRoomRole)
                .join(talkRoomRole.talkRoom, talkRoom)
                .where(talkRoomRole.talkRoom.id.eq(talkRoomId))
                .fetch();
    }

    private List<Long> toTalkRoomIds(List<TalkRoomFindAllResponse> findTalkRoom) {
        return findTalkRoom.stream()
                .map(t -> t.getTalkRoomId())
                .collect(Collectors.toList());
    }

    private Map<Long, List<TalkRoomQueryReadingStatus>> findTalkRoomRoleMap(List<Long> talkRoomIds) {
        List<TalkRoomQueryReadingStatus> talkRoomRoles = queryFactory.select(new QTalkRoomQueryReadingStatus(
                        talkRoom.id,
                        talkRoomRole.readingStatus
                ))
                .from(talkRoomRole)
                .join(talkRoomRole.talkRoom, talkRoom)
                .where(talkRoomRole.talkRoom.id.in(talkRoomIds))
                .fetch();

        return talkRoomRoles.stream()
                .collect(Collectors.groupingBy(TalkRoomQueryReadingStatus::getTalkRoomId));
    }

    private long getTotalTalkRoomCount() {
        return queryFactory
                .select(talkRoom.count())
                .from(talkRoom)
                .join(talkRoom.user, user)
                .join(talkRoom.book, book)
                .fetchOne();
    }

    private List<TalkRoomFindAllResponse> findTalkRoomBySearch(TalkRoomSearchServiceRequest search) {
        return queryFactory.select(new QTalkRoomFindAllResponse(
                        talkRoom.id.as("talkRoomId"),
                        user.name.as("userName"),
                        talkRoom.title,
                        talkRoom.content,
                        book.title,
                        book.imageUrl.as("bookImage")
                ))
                .from(talkRoom)
                .join(talkRoom.user, user)
                .join(talkRoom.book, book)
                .offset(search.getOffset())
                .limit(search.getSize())
                .orderBy(condition(search.getOrder()))
                .fetch();
    }

    private TalkRoomFindOneResponse findTalkRoomByTalkRoomId(Long talkRoomId) {
        return queryFactory.select(new QTalkRoomFindOneResponse(
                        talkRoom.id.as("talkRoomId"),
                        user.name.as("userName"),
                        talkRoom.title,
                        talkRoom.content,
                        book.title,
                        book.imageUrl.as("bookImage")
                ))
                .from(talkRoom)
                .join(talkRoom.user, user)
                .join(talkRoom.book, book)
                .where(talkRoom.id.eq(talkRoomId))
                .fetchOne();
    }

    /**
     * 아직 좋아요 기능이 구현 되지 않아 최신순으로만 정렬
     */
    private OrderSpecifier<?> condition(String order) {
//         if (order.equals("recent"))
        return talkRoom.id.desc();
    }

}
