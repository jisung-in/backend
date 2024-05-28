package com.jisungin.application.comment;

import com.jisungin.application.comment.response.CommentQueryResponse;
import com.jisungin.domain.commentimage.CommentImage;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentFindAllResponse {

    private Long commentId;
    private String userName;
    private String profileImage;
    private String content;
    private Long commentLikeCount;
    private List<String> commentImages = new ArrayList<>();
    private LocalDateTime registeredDateTime;
    private Long creatorId;

    @Builder
    private CommentFindAllResponse(Long commentId, String userName, String profileImage, String content,
                                   Long commentLikeCount, List<String> commentImages,
                                   LocalDateTime registeredDateTime, Long creatorId) {
        this.commentId = commentId;
        this.userName = userName;
        this.profileImage = profileImage;
        this.content = content;
        this.commentLikeCount = commentLikeCount;
        this.commentImages = commentImages;
        this.registeredDateTime = registeredDateTime;
        this.creatorId = creatorId;
    }

    public static CommentFindAllResponse of(CommentQueryResponse comment, List<CommentImage> commentImages) {
        return CommentFindAllResponse.builder()
                .commentId(comment.getCommentId())
                .userName(comment.getUserName())
                .profileImage(comment.getProfileImage())
                .content(comment.getContent())
                .commentLikeCount(comment.getCommentLikeCount())
                .commentImages(extractCommentImages(commentImages))
                .registeredDateTime(comment.getRegisteredDateTime().withNano(0))
                .creatorId(comment.getCreatorId())
                .build();
    }

    public static List<CommentFindAllResponse> toList(List<CommentQueryResponse> comments,
                                                      Map<Long, List<CommentImage>> commentImagesMap) {
        return comments.stream()
                .map(comment -> CommentFindAllResponse.of(comment, commentImagesMap.get(comment.getCommentId())))
                .toList();
    }

    private static List<String> extractCommentImages(List<CommentImage> commentImages) {
        return Optional.ofNullable(commentImages)
                .orElseGet(ArrayList::new)
                .stream()
                .map(CommentImage::getImageUrl)
                .toList();
    }

}
