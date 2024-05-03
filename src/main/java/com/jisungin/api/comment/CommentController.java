package com.jisungin.api.comment;

import com.jisungin.api.ApiResponse;
import com.jisungin.api.comment.request.CommentCreateRequest;
import com.jisungin.api.comment.request.CommentEditRequest;
import com.jisungin.api.oauth.Auth;
import com.jisungin.application.comment.CommentService;
import com.jisungin.application.comment.response.CommentPageResponse;
import com.jisungin.application.comment.response.CommentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/v1")
@RestController
public class CommentController {

    private final CommentService commentService;

    @PostMapping("{talkRoomId}/comments")
    public ApiResponse<CommentResponse> writeComment(@PathVariable("talkRoomId") Long talkRoomId,
                                                     @Valid @RequestBody CommentCreateRequest request,
                                                     @Auth Long userId) {
        return ApiResponse.ok(commentService.writeComment(request.toService(), talkRoomId, userId));
    }

    @GetMapping("{talkRoomId}/comments")
    public ApiResponse<CommentPageResponse> findAllComments(@PathVariable Long talkRoomId,
                                                            @Auth Long userId) {
        return ApiResponse.ok(commentService.findAllComments(talkRoomId, userId));
    }

    @PatchMapping("/comments/{commentId}")
    public ApiResponse<CommentResponse> editComment(@PathVariable Long commentId,
                                                    @Valid @RequestBody CommentEditRequest request,
                                                    @Auth Long userId) {
        return ApiResponse.ok(commentService.editComment(commentId, request.toService(), userId));
    }

    @DeleteMapping("/comments/{commentId}")
    public ApiResponse<Void> deleteComment(@PathVariable Long commentId,
                                           @Auth Long userId) {
        commentService.deleteComment(commentId, userId);

        return ApiResponse.<Void>builder()
                .message("OK")
                .status(HttpStatus.OK)
                .build();
    }

}
