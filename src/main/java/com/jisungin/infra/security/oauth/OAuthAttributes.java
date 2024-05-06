package com.jisungin.infra.security.oauth;

import com.jisungin.domain.oauth.OauthId;
import com.jisungin.domain.oauth.OauthType;
import com.jisungin.domain.user.Role;
import com.jisungin.domain.user.User;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OAuthAttributes {

    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String oauthId;
    private String name;
    private String email;
    private String picture;
    private OauthType oauthType;

    @Builder
    private OAuthAttributes(Map<String, Object> attributes, String oauthId, String nameAttributeKey, String name,
                            String email, String picture, OauthType oauthType) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.oauthId = oauthId;
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.oauthType = oauthType;
    }

    public static OAuthAttributes of(String registrationId, String usernameAttributeName,
                                     Map<String, Object> attributes) {
        return ofKakao(usernameAttributeName, attributes);
    }

    public static OAuthAttributes ofKakao(String usernameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");

        return OAuthAttributes.builder()
                .name((String) kakaoProfile.get("nickname"))
                .email((String) kakaoAccount.get("email"))
                .picture((String) kakaoProfile.get("profile_image_url"))
                .oauthId(String.valueOf(attributes.get("id")))
                .oauthType(OauthType.KAKAO)
                .attributes(attributes)
                .nameAttributeKey(usernameAttributeName)
                .build();
    }

    public User toEntity() {
        return User.builder()
                .oauthId(
                        OauthId.builder()
                                .oauthId(oauthId)
                                .oauthType(OauthType.KAKAO)
                                .build()
                )
                .name(name)
                .email(email)
                .profileImage(picture)
                .role(Role.USER)
                .build();
    }

}
