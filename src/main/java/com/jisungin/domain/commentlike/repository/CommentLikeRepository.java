package com.jisungin.domain.commentlike.repository;

import com.jisungin.domain.commentlike.CommentLike;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    Optional<CommentLike> findByCommentIdAndUserId(Long commentId, Long userId);

    @Query("select cl.comment.id from CommentLike cl where cl.user.id = :userId")
    List<Long> findCommentIdsByUserId(@Param("userId") Long userId);

    @Query(value = "SELECT cl.comment.id " +
            "FROM CommentLike cl " +
            "WHERE cl.user.id = :userId " +
            "AND cl.comment.id IN :commentIds"
    )
    List<Long> userLikeComments(@Param("userId") Long userId, @Param("commentIds") List<Long> commentIds);

}
