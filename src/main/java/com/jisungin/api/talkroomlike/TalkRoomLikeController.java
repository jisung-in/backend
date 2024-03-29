package com.jisungin.api.talkroomlike;

import com.jisungin.api.ApiResponse;
import com.jisungin.api.oauth.Auth;
import com.jisungin.api.oauth.AuthContext;
import com.jisungin.application.talkroomlike.TalkRoomLikeService;
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
public class TalkRoomLikeController {

    private final TalkRoomLikeService talkRoomLikeService;

    @PostMapping("/talk-rooms/{talkRoomId}/likes")
    public ApiResponse<Void> likeTalkRoom(@PathVariable Long talkRoomId,
                                          @Auth AuthContext authContext) {
        talkRoomLikeService.likeTalkRoom(talkRoomId, authContext);

        return ApiResponse.<Void>builder()
                .message("좋아요 성공")
                .status(HttpStatus.OK)
                .build();
    }

    @DeleteMapping("/talk-rooms/{talkRoomId}/likes")
    public ApiResponse<Void> unLikeTalkRoom(@PathVariable Long talkRoomId,
                                            @Auth AuthContext authContext) {
        talkRoomLikeService.unLikeTalkRoom(talkRoomId, authContext);

        return ApiResponse.<Void>builder()
                .message("좋아요 취소")
                .status(HttpStatus.OK)
                .build();
    }

}
