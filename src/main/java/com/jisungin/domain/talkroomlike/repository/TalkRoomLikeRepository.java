package com.jisungin.domain.talkroomlike.repository;

import com.jisungin.domain.talkroomlike.TalkRoomLike;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TalkRoomLikeRepository extends JpaRepository<TalkRoomLike, Long> {

    Optional<TalkRoomLike> findByTalkRoomIdAndUserId(Long talkRoomId, Long userId);
}
