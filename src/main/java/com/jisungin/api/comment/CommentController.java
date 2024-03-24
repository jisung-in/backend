package com.jisungin.api.comment;

import com.jisungin.api.ApiResponse;
import com.jisungin.api.comment.request.CommentCreateRequest;
import com.jisungin.api.oauth.Auth;
import com.jisungin.api.oauth.AuthContext;
import com.jisungin.application.comment.CommentService;
import com.jisungin.application.comment.response.CommentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    @PostMapping("/talk-rooms/{talkRoomId}/comments")
    public ApiResponse<CommentResponse> writeComment(@PathVariable Long talkRoomId,
                                                     @Valid @RequestBody CommentCreateRequest request,
                                                     @Auth AuthContext authContext) {
        return ApiResponse.ok(commentService.writeComment(request.toService(), talkRoomId, authContext.getUserId()));
    }

}
