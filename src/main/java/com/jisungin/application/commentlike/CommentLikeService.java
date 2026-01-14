package com.jisungin.application.commentlike;

import com.jisungin.application.commentlike.response.CommentIds;
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
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CommentLikeService {

    private final CommentLikeRepository commentLikeRepository;

    private final CommentRepository commentRepository;

    private final UserRepository userRepository;

    public CommentIds findCommentIds(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return CommentIds.of(commentLikeRepository.findCommentIdsByUserId(user.getId()));
    }

    @Transactional
    public void likeComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

        User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (commentLikeRepository.findByCommentIdAndUserId(comment.getId(), user.getId()).isPresent()) {
            throw new BusinessException(ErrorCode.LIKE_EXIST);
        }

        CommentLike commentLike = CommentLike.likeComment(comment, user);

        commentLikeRepository.save(commentLike);
    }

    @Transactional
    public void unLikeComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

        User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        CommentLike commentLike = commentLikeRepository.findByCommentIdAndUserId(comment.getId(), user.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_LIKE_NOT_FOUND));

        commentLikeRepository.delete(commentLike);
    }
}
