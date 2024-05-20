package com.jisungin.domain.talkroomlike.repository;

import com.jisungin.domain.talkroomlike.TalkRoomLike;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TalkRoomLikeRepository extends JpaRepository<TalkRoomLike, Long>, TalkRoomLikeRepositoryCustom {

    Optional<TalkRoomLike> findByTalkRoomIdAndUserId(Long talkRoomId, Long userId);

    @Query("select trl.talkRoom.id from TalkRoomLike trl where trl.user.id = :userId")
    List<Long> findTalkRoomIdsByUserId(@Param("userId") Long userId);

    @Query(value = "SELECT trl.talkRoom.id " +
            "FROM TalkRoomLike trl " +
            "WHERE trl.user.id = :userId " +
            "AND trl.talkRoom.id IN :talkRoomIds")
    List<Long> findLikeTalkRoomIdsByUserId(@Param("userId") Long userId, @Param("talkRoomIds") List<Long> talkRoomIds);

}
