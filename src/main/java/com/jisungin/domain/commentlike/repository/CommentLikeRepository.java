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

    @Query(
            "SELECT c.id FROM Comment c JOIN CommentLike cl ON c.id = "
                    + "cl.comment.id WHERE cl.user.id = :userId"
    )
    List<Long> userLikeComments(@Param("userId") Long userId);

}
