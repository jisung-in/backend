package com.jisungin.api.library;

import com.jisungin.api.ApiResponse;
import com.jisungin.api.library.request.LibraryCreateRequest;
import com.jisungin.api.library.request.LibraryEditRequest;
import com.jisungin.api.library.request.UserReadingStatusGetAllRequest;
import com.jisungin.api.support.Auth;
import com.jisungin.application.PageResponse;
import com.jisungin.application.library.LibraryService;
import com.jisungin.application.library.response.LibraryResponse;
import com.jisungin.application.library.response.UserReadingStatusResponse;
import jakarta.validation.Valid;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class LibraryController {

    private final LibraryService libraryService;

    @GetMapping("/libraries")
    public ApiResponse<List<LibraryResponse>> findLibraries(@Auth Long userId) {
        return ApiResponse.ok(libraryService.findLibraries(userId));
    }

    @PostMapping("/libraries")
    public ApiResponse<LibraryResponse> createLibrary(@Valid @RequestBody LibraryCreateRequest request,
                                                      @Auth Long userId
    ) {
        return ApiResponse.ok(libraryService.createLibrary(request.toServiceRequest(), userId));
    }

    @PatchMapping("/libraries/{libraryId}")
    public ApiResponse<Void> editLibrary(@PathVariable("libraryId") Long libraryId,
                                         @Valid @RequestBody LibraryEditRequest request,
                                         @Auth Long userId
    ) {
        libraryService.editLibrary(libraryId, userId, request.toServiceRequest());

        return ApiResponse.ok();
    }

    @DeleteMapping("/libraries/{libraryId}")
    public ApiResponse<Void> deleteLibrary(@PathVariable("libraryId") Long libraryId,
                                           @Auth Long userId
    ) {
        libraryService.deleteLibrary(libraryId, userId);

        return ApiResponse.ok();
    }

    @GetMapping("/users/libraries/statuses")
    public ApiResponse<PageResponse<UserReadingStatusResponse>> getReadingStatuses(
            @ModelAttribute UserReadingStatusGetAllRequest request,
            @Auth Long userId
    ) {
        PageResponse<UserReadingStatusResponse> response = libraryService.getUserReadingStatuses(userId, request.toService());

        return ApiResponse.ok(response);
    }
}
