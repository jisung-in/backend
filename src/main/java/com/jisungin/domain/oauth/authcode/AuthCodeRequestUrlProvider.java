package com.jisungin.domain.oauth.authcode;

import com.jisungin.domain.oauth.OauthType;

public interface AuthCodeRequestUrlProvider {

    // 인증 서버의 타입
    OauthType supportType();

    // 인증 코드 요청 주소
    String provide();

}
