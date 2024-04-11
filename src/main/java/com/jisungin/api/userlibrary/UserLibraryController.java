package com.jisungin.api.userlibrary;

import com.jisungin.api.ApiResponse;
import com.jisungin.api.oauth.Auth;
import com.jisungin.api.userlibrary.request.UserLibraryCreateRequest;
import com.jisungin.application.userlibrary.UserLibraryService;
import com.jisungin.application.userlibrary.response.UserLibraryResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class UserLibraryController {

    private final UserLibraryService userLibraryService;

    @PostMapping("/user-libraries")
    public ApiResponse<UserLibraryResponse> createUserLibraryResponse(
            @Valid @RequestBody UserLibraryCreateRequest request,
            @Auth Long userId
    ) {
        return ApiResponse.ok(userLibraryService.createUserLibrary(request.toServiceRequest(), userId));
    }

}
