package com.jisungin.api.talkroom;

import com.jisungin.api.ApiResponse;
import com.jisungin.api.oauth.Auth;
import com.jisungin.api.oauth.AuthContext;
import com.jisungin.api.talkroom.request.TalkRoomCreateRequest;
import com.jisungin.api.talkroom.request.TalkRoomEditRequest;
import com.jisungin.api.talkroom.request.TalkRoomSearchRequest;
import com.jisungin.application.PageResponse;
import com.jisungin.application.talkroom.TalkRoomService;
import com.jisungin.application.talkroom.response.TalkRoomFindAllResponse;
import com.jisungin.application.talkroom.response.TalkRoomFindOneResponse;
import com.jisungin.application.talkroom.response.TalkRoomResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/v1")
@RestController
public class TalkRoomController {

    private final TalkRoomService talkRoomService;

    @PostMapping("/talk-rooms")
    public ApiResponse<TalkRoomResponse> createTalkRoom(@Valid @RequestBody TalkRoomCreateRequest request, @Auth
    AuthContext authContext) {
        return ApiResponse.ok(talkRoomService.createTalkRoom(request.toServiceRequest(), authContext.getUserId()));
    }

    @GetMapping("/talk-rooms")
    public ApiResponse<PageResponse<TalkRoomFindAllResponse>> findAllTalkRoom(
            @ModelAttribute TalkRoomSearchRequest search) {
        return ApiResponse.ok(talkRoomService.findAllTalkRoom(search.toService()));
    }

    @GetMapping("/talk-room/{talkRoomId}")
    public ApiResponse<TalkRoomFindOneResponse> findOneTalkRoom(@PathVariable Long talkRoomId) {
        return ApiResponse.ok(talkRoomService.findOneTalkRoom(talkRoomId));
    }

    @PatchMapping("/talk-rooms")
    public ApiResponse<TalkRoomResponse> editTalkRoom(@Valid @RequestBody TalkRoomEditRequest request,
                                                      @Auth AuthContext authContext) {
        return ApiResponse.ok(talkRoomService.editTalkRoom(request.toServiceRequest(), authContext.getUserId()));
    }

    @DeleteMapping("/talk-rooms/{talkRoomId}")
    public ApiResponse<Void> deleteTalkRoom(@PathVariable Long talkRoomId, @Auth AuthContext authContext) {
        talkRoomService.deleteTalkRoom(talkRoomId, authContext.getUserId());

        return ApiResponse.<Void>builder()
                .message("OK")
                .status(HttpStatus.OK)
                .build();
    }

}
