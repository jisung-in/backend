package com.jisungin.domain.talkroomlike.repository;

import static com.jisungin.domain.talkroomlike.QTalkRoomLike.talkRoomLike;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TalkRoomLikeRepositoryImpl implements TalkRoomLikeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public boolean existsTalkRoomLike(Long talkRoomId, Long userId) {
        Integer fetchOne = queryFactory.selectOne()
                .from(talkRoomLike)
                .where(talkRoomLike.talkRoom.id.eq(talkRoomId).and(talkRoomLike.user.id.eq(userId)))
                .fetchFirst();

        return fetchOne != null;
    }
}
