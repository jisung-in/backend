package com.jisungin.domain.oauth.authcode;

import com.jisungin.domain.oauth.OauthType;
import com.jisungin.exception.BusinessException;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.jisungin.exception.ErrorCode.OAUTH_TYPE_NOT_FOUND;

@Component
public class AuthCodeRequestUrlProviderComposite {

    private final Map<OauthType, AuthCodeRequestUrlProvider> mapping;

    public AuthCodeRequestUrlProviderComposite(Set<AuthCodeRequestUrlProvider> providers) {
        mapping = providers.stream()
                .collect(Collectors.toMap(
                        AuthCodeRequestUrlProvider::supportType,
                        Function.identity()
                ));
    }

    public String provide(OauthType oauthType) {
        return getProvider(oauthType).provide();
    }

    public AuthCodeRequestUrlProvider getProvider(OauthType oauthType) {
        return Optional.ofNullable(mapping.get(oauthType))
                // TODO. 추후에 OAuth 예외 처리로 수정할 것
                .orElseThrow(() -> new BusinessException(OAUTH_TYPE_NOT_FOUND));
    }

}
