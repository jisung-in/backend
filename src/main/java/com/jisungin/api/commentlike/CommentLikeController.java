package com.jisungin.api.commentlike;

import com.jisungin.api.ApiResponse;
import com.jisungin.api.oauth.Auth;
import com.jisungin.api.oauth.AuthContext;
import com.jisungin.application.commentlike.CommentLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/v1")
@RestController
public class CommentLikeController {

    private final CommentLikeService commentLikeService;

    @PostMapping("/comments/{commentId}/likes")
    public ApiResponse<Void> likeComment(@PathVariable Long commentId,
                                         @Auth AuthContext authContext) {
        commentLikeService.likeComment(commentId, authContext.getUserId());

        return ApiResponse.<Void>builder()
                .message("좋아요 성공")
                .status(HttpStatus.OK)
                .build();
    }

}
