package com.jisungin.application.comment;

import com.jisungin.api.oauth.AuthContext;
import com.jisungin.application.PageResponse;
import com.jisungin.application.comment.request.CommentCreateServiceRequest;
import com.jisungin.application.comment.request.CommentEditServiceRequest;
import com.jisungin.application.comment.response.CommentQueryResponse;
import com.jisungin.application.comment.response.CommentResponse;
import com.jisungin.domain.comment.Comment;
import com.jisungin.domain.comment.repository.CommentRepository;
import com.jisungin.domain.commentlike.repository.CommentLikeRepository;
import com.jisungin.domain.talkroom.TalkRoom;
import com.jisungin.domain.talkroom.repository.TalkRoomRepository;
import com.jisungin.domain.user.User;
import com.jisungin.domain.user.repository.UserRepository;
import com.jisungin.exception.BusinessException;
import com.jisungin.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final TalkRoomRepository talkRoomRepository;
    private final UserRepository userRepository;
    private final CommentLikeRepository commentLikeRepository;

    @Transactional
    public CommentResponse writeComment(CommentCreateServiceRequest request, Long talkRoomId, AuthContext authContext) {
        User user = userRepository.findById(authContext.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        TalkRoom talkRoom = talkRoomRepository.findById(talkRoomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TALK_ROOM_NOT_FOUND));

        Comment comment = Comment.create(request, user, talkRoom);

        commentRepository.save(comment);

        return CommentResponse.of(comment.getContent(), user.getName());
    }

    public PageResponse<CommentQueryResponse> findAllComments(Long talkRoomId, AuthContext authContext) {
        PageResponse<CommentQueryResponse> response = commentRepository.findAllComments(talkRoomId);

        if (authContext.getUserId() != null) {
            List<Long> likeComments = commentLikeRepository.userLikeComments(authContext.getUserId());
        }

        return response;
    }

    @Transactional
    public CommentResponse editComment(Long commentId, CommentEditServiceRequest request, AuthContext authContext) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

        User user = userRepository.findById(authContext.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!comment.isCommentOwner(user.getId())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_REQUEST);
        }

        comment.edit(request.getContent());

        return CommentResponse.of(comment.getContent(), user.getName());
    }

    @Transactional
    public void deleteComment(Long commentId, AuthContext authContext) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

        User user = userRepository.findById(authContext.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!comment.isCommentOwner(user.getId())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_REQUEST);
        }

        commentRepository.delete(comment);
    }

}
