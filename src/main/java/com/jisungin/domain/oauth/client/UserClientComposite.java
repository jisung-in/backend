package com.jisungin.domain.oauth.client;

import com.jisungin.domain.oauth.OauthId;
import com.jisungin.domain.oauth.OauthType;
import com.jisungin.domain.user.User;
import com.jisungin.exception.BusinessException;
import com.jisungin.exception.ErrorCode;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class UserClientComposite {

    private final Map<OauthType, UserClient> mapping;

    public UserClientComposite(Set<UserClient> clients) {
        this.mapping = clients.stream()
                .collect(Collectors.toMap(
                        UserClient::supportType,
                        Function.identity()
                ));
    }

    public User fetch(OauthType oauthType, String authCode) {
        return getClient(oauthType).fetch(authCode);
    }

    public void logout(OauthType oauthType, OauthId oauthId) {
        getClient(oauthType).logout(oauthId.getOauthId());
    }

    private UserClient getClient(OauthType oauthType) {
        return Optional.ofNullable(mapping.get(oauthType))
                // TODO. 추후에 OAuth 예외 처리로 수정할 것
                .orElseThrow(() -> new BusinessException(ErrorCode.OAUTH_TYPE_NOT_FOUND));
    }

}
