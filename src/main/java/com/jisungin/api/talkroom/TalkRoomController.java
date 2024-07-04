package com.jisungin.api.talkroom;

import com.jisungin.api.ApiResponse;
import com.jisungin.api.support.Auth;
import com.jisungin.api.talkroom.request.TalkRoomCreateRequest;
import com.jisungin.api.talkroom.request.TalkRoomEditRequest;
import com.jisungin.application.OffsetLimit;
import com.jisungin.application.PageResponse;
import com.jisungin.application.SliceResponse;
import com.jisungin.application.talkroom.TalkRoomService;
import com.jisungin.application.talkroom.request.TalkRoomSearchCondition;
import com.jisungin.application.talkroom.request.UserTalkRoomSearchCondition;
import com.jisungin.application.talkroom.response.TalkRoomFindAllResponse;
import com.jisungin.application.talkroom.response.TalkRoomFindOneResponse;
import com.jisungin.application.talkroom.response.TalkRoomRelatedBookResponse;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/v1")
@RestController
public class TalkRoomController {

    private final TalkRoomService talkRoomService;

    @GetMapping("/books/{isbn}/talk-rooms")
    public ApiResponse<PageResponse<TalkRoomRelatedBookResponse>> findBookTalkRooms(
            @PathVariable(name = "isbn") String isbn,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "5") Integer size
    ) {
        return ApiResponse.ok(talkRoomService.findBookTalkRooms(isbn, OffsetLimit.of(page, size)));
    }

    @PostMapping("/talk-rooms")
    public ApiResponse<TalkRoomFindOneResponse> createTalkRoom(@Valid @RequestBody TalkRoomCreateRequest request,
                                                               @Auth Long userId
    ) {
        LocalDateTime registeredDateTime = LocalDateTime.now();
        return ApiResponse.ok(talkRoomService.createTalkRoom(request.toServiceRequest(), userId, registeredDateTime));
    }

    @GetMapping("/talk-rooms")
    public ApiResponse<SliceResponse<TalkRoomFindAllResponse>> findAllTalkRoom(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(value = "order", required = false, defaultValue = "recent") String order,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "day", required = false) String day
    ) {
        LocalDateTime now = LocalDateTime.now();

        return ApiResponse.ok(talkRoomService.findAllTalkRoom(OffsetLimit.of(page, size, order),
                TalkRoomSearchCondition.of(search, day), now));
    }

    @GetMapping("/talk-rooms/{talkRoomId}")
    public ApiResponse<TalkRoomFindOneResponse> findOneTalkRoom(@PathVariable Long talkRoomId) {
        return ApiResponse.ok(talkRoomService.findOneTalkRoom(talkRoomId));
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

    @GetMapping("/users/talk-rooms")
    public ApiResponse<PageResponse<TalkRoomFindAllResponse>> findUserTalkRoom(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(value = "userTalkRoomsFilter", required = false) boolean userTalkRoomsFilter,
            @RequestParam(value = "commentedFilter", required = false) boolean commentedFilter,
            @RequestParam(value = "likedFilter", required = false) boolean likedFilter,
            @Auth Long userId
    ) {
        return ApiResponse.ok(talkRoomService.findUserTalkRoom(OffsetLimit.of(page, size),
                UserTalkRoomSearchCondition.of(userTalkRoomsFilter, commentedFilter, likedFilter), userId));
    }

}
