package com.jisungin.domain.talkroom.repository;

import com.jisungin.domain.talkroom.TalkRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TalkRoomRepository extends JpaRepository<TalkRoom, Long> {

}
