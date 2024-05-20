package com.jisungin.api.talkroomlike;

import com.jisungin.api.ApiResponse;
import com.jisungin.api.support.Auth;
import com.jisungin.application.talkroomlike.TalkRoomLikeService;
import com.jisungin.application.talkroomlike.response.TalkRoomIds;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/v1")
@RestController
public class TalkRoomLikeController {

    private final TalkRoomLikeService talkRoomLikeService;

    @GetMapping("/talk-rooms/likes")
    public ApiResponse<TalkRoomIds> findTalkRoomIds(@Auth Long userId) {
        return ApiResponse.ok(talkRoomLikeService.findTalkRoomIds(userId));
    }

    @PostMapping("/talk-rooms/{talkRoomId}/likes")
    public ApiResponse<Void> likeTalkRoom(@PathVariable Long talkRoomId,
                                          @Auth Long userId) {
        talkRoomLikeService.likeTalkRoom(talkRoomId, userId);

        return ApiResponse.<Void>builder()
                .message("좋아요 성공")
                .status(HttpStatus.OK)
                .build();
    }

    @DeleteMapping("/talk-rooms/{talkRoomId}/likes")
    public ApiResponse<Void> unLikeTalkRoom(@PathVariable Long talkRoomId,
                                            @Auth Long userId) {
        talkRoomLikeService.unLikeTalkRoom(talkRoomId, userId);

        return ApiResponse.<Void>builder()
                .message("좋아요 취소")
                .status(HttpStatus.OK)
                .build();
    }

}
