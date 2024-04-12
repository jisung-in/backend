package com.jisungin.api.userlibrary;

import com.jisungin.api.ApiResponse;
import com.jisungin.api.oauth.Auth;
import com.jisungin.api.userlibrary.request.UserLibraryCreateRequest;
import com.jisungin.api.userlibrary.request.UserLibraryEditRequest;
import com.jisungin.application.userlibrary.UserLibraryService;
import com.jisungin.application.userlibrary.response.UserLibraryResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class UserLibraryController {

    private final UserLibraryService userLibraryService;

    @GetMapping("/user-libraries")
    public ApiResponse<UserLibraryResponse> getUserLibrary(@RequestParam String isbn,
                                                           @Auth Long userId
    ) {
        return ApiResponse.ok(userLibraryService.getUserLibrary(userId, isbn));
    }

    @PostMapping("/user-libraries")
    public ApiResponse<UserLibraryResponse> createUserLibrary(@Valid @RequestBody UserLibraryCreateRequest request,
                                                              @Auth Long userId
    ) {
        return ApiResponse.ok(userLibraryService.createUserLibrary(request.toServiceRequest(), userId));
    }

    @PatchMapping("/user-libraries/{userLibraryId}")
    public ApiResponse<Void> editUserLibrary(@PathVariable("userLibraryId") Long userLibraryId,
                                             @Valid @RequestBody UserLibraryEditRequest request,
                                             @Auth Long userId
    ) {
        userLibraryService.editUserLibrary(userLibraryId, userId, request.toServiceRequest());

        return ApiResponse.ok();
    }

    @DeleteMapping("/user-libraries/{userLibraryId}")
    public ApiResponse<Void> deleteUserLibrary(@PathVariable("userLibraryId") Long userLibraryId,
                                               @RequestParam String isbn,
                                               @Auth Long userId
    ) {
        userLibraryService.deleteUserLibrary(userLibraryId, userId, isbn);

        return ApiResponse.ok();
    }

}
