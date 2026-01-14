package com.jisungin.domain.comment.repository;

import com.jisungin.domain.comment.Comment;
import com.jisungin.domain.talkroom.TalkRoom;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {

    Optional<Comment> findByTalkRoom(TalkRoom talkRoom);
}
