package com.jisungin.domain.talkroom.repository;

import com.jisungin.domain.talkroom.TalkRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TalkRoomRepository extends JpaRepository<TalkRoom, Long> {

    @Query(
            "select t from TalkRoom t join t.user u where t.id = :talkRoomId"
    )
    TalkRoom findByIdWithUser(@Param("talkRoomId") Long talkRoomId);
}
