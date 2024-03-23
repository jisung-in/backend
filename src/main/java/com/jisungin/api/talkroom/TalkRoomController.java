package com.jisungin.api.talkroom;

import com.jisungin.api.ApiResponse;
import com.jisungin.api.talkroom.request.TalkRoomCreateRequest;
import com.jisungin.api.talkroom.request.TalkRoomEditRequest;
import com.jisungin.api.talkroom.request.TalkRoomSearchRequest;
import com.jisungin.application.response.PageResponse;
import com.jisungin.application.talkroom.TalkRoomService;
import com.jisungin.application.talkroom.response.TalkRoomQueryResponse;
import com.jisungin.application.talkroom.response.TalkRoomResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/v1")
@RestController
public class TalkRoomController {

    private final TalkRoomService talkRoomService;

    // TODO. 회원 도메인이 개발되면 변경 예정
    @PostMapping("/talk-rooms")
    public ApiResponse<TalkRoomResponse> createTalkRoom(@Valid @RequestBody TalkRoomCreateRequest request) {
        return ApiResponse.ok(talkRoomService.createTalkRoom(request.toServiceRequest(), "user@gmail.com"));
    }

    @GetMapping("/talk-rooms")
    public ApiResponse<PageResponse<TalkRoomQueryResponse>> getTalkRooms(@ModelAttribute TalkRoomSearchRequest search) {
        return ApiResponse.ok(talkRoomService.getTalkRooms(search.toService()));
    }

    @PatchMapping("/talk-rooms")
    public ApiResponse<TalkRoomResponse> editTalkRoom(@Valid @RequestBody TalkRoomEditRequest request) {
        return ApiResponse.ok(talkRoomService.editTalkRoom(request.toServiceRequest(), "user@gmail.com"));
    }

}
