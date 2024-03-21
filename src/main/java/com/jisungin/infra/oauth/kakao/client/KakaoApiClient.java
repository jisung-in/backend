package com.jisungin.infra.oauth.kakao.client;

import com.jisungin.infra.oauth.kakao.dto.KakaoToken;
import com.jisungin.infra.oauth.kakao.dto.KakaoUserResponse;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.*;

public interface KakaoApiClient {

    @PostExchange(url = "https://kauth.kakao.com/oauth/token", contentType = APPLICATION_FORM_URLENCODED_VALUE)
    KakaoToken fetchToken(@RequestParam MultiValueMap<String, String> params);

    @GetExchange(url = "https://kapi.kakao.com/v2/user/me")
    KakaoUserResponse fetchUser(@RequestHeader(name = AUTHORIZATION) String bearerToken);

    @PostExchange(url = "https://kapi.kakao.com/v1/user/logout")
    void logoutUser(@RequestHeader(name = AUTHORIZATION) String adminKey,
                    @RequestBody MultiValueMap<String, String> params
    );

}
