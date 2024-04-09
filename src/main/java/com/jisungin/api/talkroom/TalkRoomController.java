package com.jisungin.api.talkroom;

import com.jisungin.api.ApiResponse;
import com.jisungin.api.SearchRequest;
import com.jisungin.api.oauth.Auth;
import com.jisungin.api.talkroom.request.TalkRoomCreateRequest;
import com.jisungin.api.talkroom.request.TalkRoomEditRequest;
import com.jisungin.application.talkroom.TalkRoomService;
import com.jisungin.application.talkroom.response.TalkRoomFindOneResponse;
import com.jisungin.application.talkroom.response.TalkRoomPageResponse;
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
    public ApiResponse<TalkRoomFindOneResponse> createTalkRoom(@Valid @RequestBody TalkRoomCreateRequest request, @Auth
    Long userId) {
        return ApiResponse.ok(talkRoomService.createTalkRoom(request.toServiceRequest(), userId));
    }

    @GetMapping("/talk-rooms")
    public ApiResponse<TalkRoomPageResponse> findAllTalkRoom(
            @ModelAttribute SearchRequest search, @Auth Long userId) {
        return ApiResponse.ok(talkRoomService.findAllTalkRoom(search.toService(), userId));
    }

    @GetMapping("/talk-room/{talkRoomId}")
    public ApiResponse<TalkRoomFindOneResponse> findOneTalkRoom(@PathVariable Long talkRoomId,
                                                                @Auth Long userId) {
        return ApiResponse.ok(talkRoomService.findOneTalkRoom(talkRoomId, userId));
    }

    @PatchMapping("/talk-rooms")
    public ApiResponse<Void> editTalkRoom(@Valid @RequestBody TalkRoomEditRequest request,
                                          @Auth Long userId) {
        talkRoomService.editTalkRoom(request.toServiceRequest(), userId);

        return ApiResponse.<Void>builder()
                .message("수정 완료")
                .status(HttpStatus.OK)
                .build();
    }

    @DeleteMapping("/talk-rooms/{talkRoomId}")
    public ApiResponse<Void> deleteTalkRoom(@PathVariable Long talkRoomId, @Auth Long userId) {
        talkRoomService.deleteTalkRoom(talkRoomId, userId);

        return ApiResponse.<Void>builder()
                .message("삭제 성공")
                .status(HttpStatus.OK)
                .build();
    }

}
