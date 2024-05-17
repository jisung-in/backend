package com.jisungin.application.comment;

import com.jisungin.application.PageResponse;
import com.jisungin.application.comment.request.CommentCreateServiceRequest;
import com.jisungin.application.comment.request.CommentEditServiceRequest;
import com.jisungin.application.comment.response.CommentPageResponse;
import com.jisungin.application.comment.response.CommentQueryResponse;
import com.jisungin.application.comment.response.CommentResponse;
import com.jisungin.domain.ReadingStatus;
import com.jisungin.domain.comment.Comment;
import com.jisungin.domain.comment.repository.CommentRepository;
import com.jisungin.domain.commentimage.CommentImage;
import com.jisungin.domain.commentimage.repository.CommentImageRepository;
import com.jisungin.domain.commentlike.repository.CommentLikeRepository;
import com.jisungin.domain.talkroom.TalkRoom;
import com.jisungin.domain.talkroom.repository.TalkRoomRepository;
import com.jisungin.domain.talkroom.repository.TalkRoomRoleRepository;
import com.jisungin.domain.user.User;
import com.jisungin.domain.user.repository.UserRepository;
import com.jisungin.domain.userlibrary.repository.UserLibraryRepository;
import com.jisungin.exception.BusinessException;
import com.jisungin.exception.ErrorCode;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
    private final UserLibraryRepository userLibraryRepository;
    private final TalkRoomRoleRepository talkRoomRoleRepository;
    private final CommentImageRepository commentImageRepository;

    @Transactional
    public CommentResponse writeComment(CommentCreateServiceRequest request, Long talkRoomId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        TalkRoom talkRoom = talkRoomRepository.findById(talkRoomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TALK_ROOM_NOT_FOUND));

        Optional<ReadingStatus> userReadingStatus = userLibraryRepository.findByUserId(user.getId());

        List<ReadingStatus> talkRoomReadingStatus = talkRoomRoleRepository.findTalkRoomRoleByTalkRoomId(
                talkRoom.getId());

        checkPermissionToWriteComment(talkRoomReadingStatus, userReadingStatus.orElse(ReadingStatus.NONE));

        Comment comment = Comment.create(request, user, talkRoom);

        commentRepository.save(comment);

        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            request.getImageUrls().stream()
                    .map(url -> CommentImage.createImages(comment, url))
                    .forEach(commentImageRepository::save);
        }

        List<String> imageUrls = commentImageRepository.findByCommentIdWithImageUrl(comment.getId());

        return CommentResponse.of(comment.getContent(), user.getName(), imageUrls);

    }

    public CommentPageResponse findAllComments(Long talkRoomId, Long userId) {
        TalkRoom talkRoom = talkRoomRepository.findById(talkRoomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TALK_ROOM_NOT_FOUND));

        User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        List<CommentQueryResponse> findComment = commentRepository.findAllComments(talkRoom.getId());

        Long totalCount = commentRepository.commentTotalCount(talkRoom.getId());

        List<Long> userLikeCommentIds =
                (user.getId() != null) ? commentLikeRepository.userLikeComments(user.getId()) : Collections.emptyList();

        return CommentPageResponse.of(PageResponse.of(findComment.size(), totalCount, findComment), userLikeCommentIds);
    }

    @Transactional
    public CommentResponse editComment(Long commentId, CommentEditServiceRequest request, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!comment.isCommentOwner(user.getId())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_REQUEST);
        }

        comment.edit(request.getContent());

        if (request.getNewImage() != null && !request.getNewImage().isEmpty()) {
            request.getNewImage().stream().map(url -> CommentImage.createImages(comment, url))
                    .forEach(commentImageRepository::save);
        }

        if (request.getRemoveImage() != null && !request.getRemoveImage().isEmpty()) {
            request.getRemoveImage().stream().map(url -> commentImageRepository.findByCommentAndImageUrl(comment, url))
                    .forEach(commentImageRepository::deleteAll);
        }

        List<String> imageUrls = commentImageRepository.findByCommentIdWithImageUrl(comment.getId());

        return CommentResponse.of(comment.getContent(), user.getName(), imageUrls);
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!comment.isCommentOwner(user.getId())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_REQUEST);
        }

        List<CommentImage> images = commentImageRepository.findByComment(comment);
        if (images != null && !images.isEmpty()) {
            commentImageRepository.deleteAll(images);
        }
        commentRepository.delete(comment);
    }

    private void checkPermissionToWriteComment(List<ReadingStatus> talkRoomReadingStatus,
                                               ReadingStatus userReadingStatus) {
        if (!talkRoomReadingStatus.contains(ReadingStatus.NONE) && !talkRoomReadingStatus.contains(userReadingStatus)) {
            throw new BusinessException(ErrorCode.UNABLE_WRITE_COMMENT);
        }
    }

}
