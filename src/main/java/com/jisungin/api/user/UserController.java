package com.jisungin.api.user;

import com.jisungin.api.ApiResponse;
import com.jisungin.api.support.Auth;
import com.jisungin.application.user.UserService;
import com.jisungin.application.user.response.UserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/v1/users")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ApiResponse<UserInfoResponse> getUserInfo(@Auth Long userId) {
        UserInfoResponse userInfo = userService.getUserInfo(userId);
        return ApiResponse.ok(userInfo);
    }
}
