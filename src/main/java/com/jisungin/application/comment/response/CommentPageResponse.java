package com.jisungin.application.comment.response;

import com.jisungin.application.PageResponse;
import com.jisungin.application.comment.CommentFindAllResponse;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentPageResponse {

    private PageResponse<CommentFindAllResponse> response;

    private List<Long> userLikeCommentIds = new ArrayList<>();

    @Builder
    private CommentPageResponse(PageResponse<CommentFindAllResponse> response, List<Long> userLikeCommentIds) {
        this.response = response;
        this.userLikeCommentIds = userLikeCommentIds;
    }

    public static CommentPageResponse of(PageResponse<CommentFindAllResponse> response,
                                         List<Long> userLikeCommentIds) {
        return CommentPageResponse.builder()
                .response(response)
                .userLikeCommentIds(userLikeCommentIds)
                .build();
    }

}
