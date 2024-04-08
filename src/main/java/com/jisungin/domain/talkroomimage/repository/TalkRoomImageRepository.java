package com.jisungin.domain.talkroomimage.repository;

import com.jisungin.domain.talkroom.TalkRoom;
import com.jisungin.domain.talkroomimage.TalkRoomImage;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TalkRoomImageRepository extends JpaRepository<TalkRoomImage, Long>, TalkRoomImageRepositoryCustom {
    List<TalkRoomImage> findByTalkRoom(TalkRoom talkRoom);

    @Query(
            "select ti.imageUrl from TalkRoomImage ti where ti.talkRoom.id = :talkRoomId"
    )
    List<String> findByTalkRoomIdWithImageUrl(@Param("talkRoomId") Long talkRoomId);

    List<TalkRoomImage> findByTalkRoomAndImageUrl(TalkRoom talkRoom, String image);

}
