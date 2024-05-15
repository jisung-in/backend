package com.jisungin.api.commentlike;

import com.jisungin.api.ApiResponse;
import com.jisungin.api.support.Auth;
import com.jisungin.application.commentlike.CommentLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
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
                                         @Auth Long userId) {
        commentLikeService.likeComment(commentId, userId);

        return ApiResponse.<Void>builder()
                .message("좋아요 성공")
                .status(HttpStatus.OK)
                .build();
    }

    @DeleteMapping("/comments/{commentId}/likes")
    public ApiResponse<Void> unLikeComment(@PathVariable Long commentId,
                                           @Auth Long userId) {
        commentLikeService.unLikeComment(commentId, userId);

        return ApiResponse.<Void>builder()
                .message("좋아요 취소")
                .status(HttpStatus.OK)
                .build();
    }

}
