package com.jisungin.domain.talkroomimage.repository;

import static com.jisungin.domain.talkroom.QTalkRoom.talkRoom;
import static com.jisungin.domain.talkroomimage.QTalkRoomImage.talkRoomImage;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TalkRoomImageRepositoryImpl implements TalkRoomImageRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<String> findTalkRoomImages(Long talkRoomId) {
        return queryFactory.select(talkRoomImage.imageUrl)
                .from(talkRoomImage)
                .join(talkRoomImage.talkRoom, talkRoom).on(talkRoomImage.talkRoom.eq(talkRoom))
                .where(talkRoomImage.talkRoom.id.eq(talkRoomId))
                .fetch();
    }

}
