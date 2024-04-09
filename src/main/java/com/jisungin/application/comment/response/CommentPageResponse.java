package com.jisungin.application.comment.response;

import com.jisungin.application.PageResponse;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentPageResponse {

    private PageResponse<CommentQueryResponse> response;

    private List<Long> userLikeCommentIds = new ArrayList<>();

    @Builder
    private CommentPageResponse(PageResponse<CommentQueryResponse> response, List<Long> userLikeCommentIds) {
        this.response = response;
        this.userLikeCommentIds = userLikeCommentIds;
    }

    public static CommentPageResponse of(PageResponse<CommentQueryResponse> response,
                                         List<Long> userLikeCommentIds) {
        return CommentPageResponse.builder()
                .response(response)
                .userLikeCommentIds(userLikeCommentIds)
                .build();
    }

}
