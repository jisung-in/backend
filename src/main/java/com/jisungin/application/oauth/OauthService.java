package com.jisungin.application.oauth;

import com.jisungin.domain.oauth.OauthType;
import com.jisungin.domain.oauth.authcode.AuthCodeRequestUrlProviderComposite;
import com.jisungin.domain.oauth.client.UserClientComposite;
import com.jisungin.domain.user.User;
import com.jisungin.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OauthService {

    private final AuthCodeRequestUrlProviderComposite authCodeRequestUrlProviderComposite;
    private final UserClientComposite userClientComposite;
    private final UserRepository userRepository;

    public String getAuthCodeRequestUrl(OauthType oauthType) {
        return authCodeRequestUrlProviderComposite.provide(oauthType);
    }

    public Long login(OauthType oauthType, String authCode) {
        User user = userClientComposite.fetch(oauthType, authCode);
        User savedUser = userRepository.findByOauthId(user.getOauthId())
                .orElseGet(() -> userRepository.save(user));
        // TODO. 추후에 다른 식별자로 구현할 예정
        return savedUser.getId();
    }

}
