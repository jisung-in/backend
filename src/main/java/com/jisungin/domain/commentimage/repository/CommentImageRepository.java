package com.jisungin.domain.commentimage.repository;

import com.jisungin.domain.comment.Comment;
import com.jisungin.domain.commentimage.CommentImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentImageRepository extends JpaRepository<CommentImage, Long>, CommentImageRepositoryCustom {

    @Query(
            "select ci.imageUrl from CommentImage ci where ci.comment.id = :commentId"
    )
    List<String> findByCommentIdWithImageUrl(@Param("commentId") Long commentId);

    List<CommentImage> findByCommentAndImageUrl(Comment comment, String url);

    List<CommentImage> findByComment(Comment comment);
}
