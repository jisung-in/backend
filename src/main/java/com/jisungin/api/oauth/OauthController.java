package com.jisungin.api.oauth;

import com.jisungin.api.ApiResponse;
import com.jisungin.application.oauth.OauthService;
import com.jisungin.domain.oauth.OauthType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.*;

import static com.jisungin.api.oauth.AuthConstant.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/oauth")
public class OauthController {

    private final OauthService oauthService;

    @SneakyThrows
    @GetMapping("/{oauthType}")
    public ApiResponse<Void> redirectAuthRequestUrl(
            @PathVariable OauthType oauthType,
            HttpServletResponse response
    ) {
        String redirectUrl = oauthService.getAuthCodeRequestUrl(oauthType);
        response.sendRedirect(redirectUrl);
        return ApiResponse.ok(null);
    }

    @GetMapping("/login/{oauthType}")
    public ApiResponse<Void> login(
            @PathVariable OauthType oauthType,
            @RequestParam("code") String code,
            HttpServletRequest request
    ) {
        Long userId = oauthService.login(oauthType, code);
        HttpSession session = request.getSession(true);
        session.setAttribute(JSESSION_ID, userId);
        return ApiResponse.ok(null);
    }

    @GetMapping("/logout/{oauthType}")
    public ApiResponse<Void> logout(
            @Auth Long userId,
            @PathVariable OauthType oauthType,
            HttpServletRequest request
    ) {
        oauthService.logout(oauthType, userId);
        request.getSession(false).invalidate();
        return ApiResponse.ok(null);
    }

}
