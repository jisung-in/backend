package com.jisungin.domain.talkroom.repository;

import static com.jisungin.domain.talkroom.QTalkRoomRole.talkRoomRole;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;

import com.jisungin.domain.ReadingStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TalkRoomRoleRepositoryImpl implements TalkRoomRoleRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Map<Long, List<ReadingStatus>> findTalkRoomRoleByIds(List<Long> talkRoomIds) {
        return queryFactory.select(talkRoomRole.talkRoom.id, talkRoomRole.readingStatus)
                .from(talkRoomRole)
                .where(talkRoomRole.talkRoom.id.in(talkRoomIds))
                .transform(groupBy(talkRoomRole.talkRoom.id).as(list(talkRoomRole.readingStatus)));
    }

}
