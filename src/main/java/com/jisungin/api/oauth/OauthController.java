package com.jisungin.api.oauth;

import com.jisungin.api.ApiResponse;
import com.jisungin.application.oauth.OauthService;
import com.jisungin.domain.oauth.OauthType;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.*;

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
    public ApiResponse<Long> login(
            @PathVariable OauthType oauthType,
            @RequestParam("code") String code
    ) {
        Long login = oauthService.login(oauthType, code);
        return ApiResponse.ok(login);
    }

}
