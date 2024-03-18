package com.jisungin.domain.talkroom.repository;

import com.jisungin.domain.talkroom.TalkRoom;
import com.jisungin.domain.talkroom.TalkRoomRole;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TalkRoomRoleRepository extends JpaRepository<TalkRoomRole, Long> {

    List<TalkRoomRole> findByTalkRoom(TalkRoom talkRoom);

    List<TalkRoomRole> findAllByTalkRoom(TalkRoom talkRoom);

    void deleteAllByTalkRoom(TalkRoom talkRoom);
}
