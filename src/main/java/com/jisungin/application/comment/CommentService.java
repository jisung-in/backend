package com.jisungin.application.comment;

import com.jisungin.application.comment.request.CommentCreateServiceRequest;
import com.jisungin.application.comment.response.CommentResponse;
import com.jisungin.domain.comment.Comment;
import com.jisungin.domain.comment.repository.CommentRepository;
import com.jisungin.domain.talkroom.TalkRoom;
import com.jisungin.domain.talkroom.repository.TalkRoomRepository;
import com.jisungin.domain.user.User;
import com.jisungin.domain.user.repository.UserRepository;
import com.jisungin.exception.BusinessException;
import com.jisungin.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final TalkRoomRepository talkRoomRepository;
    private final UserRepository userRepository;

    public CommentResponse writeComment(CommentCreateServiceRequest request, Long talkRoomId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        TalkRoom talkRoom = talkRoomRepository.findById(talkRoomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TALK_ROOM_NOT_FOUND));

        Comment comment = Comment.create(request, user, talkRoom);

        commentRepository.save(comment);

        return CommentResponse.of(comment.getContent(), user.getName());
    }

}
