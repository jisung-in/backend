package com.jisungin.application.commentlike;

import com.jisungin.domain.comment.Comment;
import com.jisungin.domain.comment.repository.CommentRepository;
import com.jisungin.domain.commentlike.CommentLike;
import com.jisungin.domain.commentlike.repository.CommentLikeRepository;
import com.jisungin.domain.user.User;
import com.jisungin.domain.user.repository.UserRepository;
import com.jisungin.exception.BusinessException;
import com.jisungin.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CommentLikeService {

    private final CommentLikeRepository commentLikeRepository;

    private final CommentRepository commentRepository;

    private final UserRepository userRepository;

    public void likeComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

        User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (commentLikeRepository.findByCommentIdAndUserId(commentId, userId).isPresent()) {
            throw new BusinessException(ErrorCode.LIKE_EXIST);
        }

        CommentLike commentLike = CommentLike.likeComment(comment, user);

        commentLikeRepository.save(commentLike);
    }

}
