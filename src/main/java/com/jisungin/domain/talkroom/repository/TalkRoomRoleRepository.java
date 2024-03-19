package com.jisungin.domain.talkroom.repository;

import com.jisungin.domain.talkroom.TalkRoom;
import com.jisungin.domain.talkroom.TalkRoomRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TalkRoomRoleRepository extends JpaRepository<TalkRoomRole, Long> {

    void deleteAllByTalkRoom(TalkRoom talkRoom);

}
