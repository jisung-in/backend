package com.jisungin.application.comment;

import com.jisungin.application.comment.response.CommentQueryResponse;
import com.jisungin.domain.commentimage.CommentImage;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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

    @Builder
    private CommentFindAllResponse(Long commentId, String userName, String profileImage, String content,
                                   Long commentLikeCount, List<String> commentImages,
                                   LocalDateTime registeredDateTime) {
        this.commentId = commentId;
        this.userName = userName;
        this.profileImage = profileImage;
        this.content = content;
        this.commentLikeCount = commentLikeCount;
        this.commentImages = commentImages;
        this.registeredDateTime = registeredDateTime;
    }

    public static List<CommentFindAllResponse> create(List<CommentQueryResponse> comments,
                                                      Map<Long, List<CommentImage>> commentImages) {
        return comments.stream()
                .map(comment -> {
                    List<String> commentImageUrls = extractCommentImages(commentImages, comment);

                    return CommentFindAllResponse.builder()
                            .commentId(comment.getCommentId())
                            .userName(comment.getUserName())
                            .profileImage(comment.getProfileImage())
                            .content(comment.getContent())
                            .commentLikeCount(comment.getCommentLikeCount())
                            .commentImages(commentImageUrls)
                            .registeredDateTime(comment.getCreateTime())
                            .build();
                })
                .collect(Collectors.toList());
    }

    private static List<String> extractCommentImages(Map<Long, List<CommentImage>> commentImages,
                                                     CommentQueryResponse comment) {
        if (commentImages.isEmpty()) {
            return new ArrayList<>();
        }
        return commentImages.get(comment.getCommentId()).stream()
                .map(CommentImage::getImageUrl)
                .toList();
    }
}
