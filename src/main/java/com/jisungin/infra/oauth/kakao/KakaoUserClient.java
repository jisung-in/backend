package com.jisungin.infra.oauth.kakao;

import com.jisungin.domain.oauth.OauthType;
import com.jisungin.domain.oauth.client.UserClient;
import com.jisungin.domain.user.User;
import com.jisungin.infra.oauth.kakao.client.KakaoApiClient;
import com.jisungin.infra.oauth.kakao.dto.KakaoToken;
import com.jisungin.infra.oauth.kakao.dto.KakaoUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Component
@RequiredArgsConstructor
public class KakaoUserClient implements UserClient {

    private final KakaoApiClient kakaoApiClient;
    private final KakaoOauthConfig kakaoOauthConfig;

    @Override
    public OauthType supportType() {
        return OauthType.KAKAO;
    }

    @Override
    public User fetch(String authCode) {
        KakaoToken tokenInfo = kakaoApiClient.fetchToken(tokenRequestParams(authCode));
        KakaoUserResponse kakaoUserResponse = kakaoApiClient.fetchUser("Bearer " + tokenInfo.accessToken());
        return kakaoUserResponse.toEntity();
    }

    private MultiValueMap<String, String> tokenRequestParams(String authCode) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoOauthConfig.clientId());
        params.add("redirect_uri", kakaoOauthConfig.redirectUri());
        params.add("code", authCode);
        params.add("client_secret", kakaoOauthConfig.clientSecret());
        return params;
    }

}
