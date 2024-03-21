package com.jisungin.domain.oauth.client;

import com.jisungin.domain.oauth.OauthType;
import com.jisungin.domain.user.User;

public interface UserClient {

    OauthType supportType();

    User fetch(String authCode);

    void logout(String oauthId);

}
