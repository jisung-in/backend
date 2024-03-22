package com.jisungin.domain.talkroom.repository;

import static com.jisungin.domain.book.QBook.book;
import static com.jisungin.domain.talkroom.QTalkRoom.talkRoom;
import static com.jisungin.domain.talkroom.QTalkRoomRole.talkRoomRole;
import static com.jisungin.domain.user.QUser.user;

import com.jisungin.application.response.PageResponse;
import com.jisungin.application.talkroom.request.TalkRoomSearchServiceRequest;
import com.jisungin.application.talkroom.response.QTalkRoomQueryReadingStatus;
import com.jisungin.application.talkroom.response.QTalkRoomQueryResponse;
import com.jisungin.application.talkroom.response.TalkRoomQueryReadingStatus;
import com.jisungin.application.talkroom.response.TalkRoomQueryResponse;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TalkRoomRepositoryImpl implements TalkRoomRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public PageResponse getTalkRooms(TalkRoomSearchServiceRequest search) {

        List<TalkRoomQueryResponse> findTalkRoom = findTalkRoom(search);

        findTalkRoom.forEach(t -> {
            List<TalkRoomQueryReadingStatus> talkRoomReadingStatus = findTalkRoomReadingStatus(t.getTalkRoomId());

            t.addTalkRoomStatus(talkRoomReadingStatus);
        });

        long totalCount = getTotalTalkRoomCount();

        return PageResponse.<TalkRoomQueryResponse>builder()
                .queryResponse(findTalkRoom)
                .totalCount(totalCount)
                .size(search.getSize())
                .build();
    }

    private long getTotalTalkRoomCount() {
        return queryFactory
                .select(talkRoom.count())
                .from(talkRoom)
                .join(talkRoom.user, user)
                .join(talkRoom.book, book)
                .fetchOne();
    }

    private List<TalkRoomQueryResponse> findTalkRoom(TalkRoomSearchServiceRequest search) {
        return queryFactory.select(new QTalkRoomQueryResponse(
                        talkRoom.id.as("talkRoomId"),
                        user.name.as("userName"),
                        talkRoom.content,
                        book.url.as("bookImage")
                ))
                .from(talkRoom)
                .join(talkRoom.user, user)
                .join(talkRoom.book, book)
                .offset(search.getOffset())
                .limit(search.getSize())
                .orderBy(condition(search.getOrder()))
                .fetch();
    }

    private List<TalkRoomQueryReadingStatus> findTalkRoomReadingStatus(Long talkRoomId) {
        return queryFactory.select(new QTalkRoomQueryReadingStatus(
                        talkRoomRole.readingStatus
                ))
                .from(talkRoomRole)
                .join(talkRoomRole.talkRoom, talkRoom)
                .where(talkRoomRole.talkRoom.id.eq(talkRoomId))
                .fetch();
    }

    /**
     * 아직 좋아요 기능이 구현 되지 않아 최신순으로만 정렬
     */
    private OrderSpecifier<?> condition(String order) {
//         if (order.equals("recent"))
        return talkRoom.id.desc();
    }

}
