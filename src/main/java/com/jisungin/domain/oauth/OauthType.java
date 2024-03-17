package com.jisungin.domain.oauth;

import java.util.Locale;

public enum OauthType {

    KAKAO;

    public static OauthType fromName(String name) {
        return OauthType.valueOf(name.toUpperCase(Locale.ENGLISH));
    }

}
