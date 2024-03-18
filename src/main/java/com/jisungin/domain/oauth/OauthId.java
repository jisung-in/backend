package com.jisungin.domain.oauth;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class OauthId {

    @Column(nullable = false, name = "oauth_id")
    private String oauthId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "oauth_type")
    private OauthType oauthType;

    @Builder
    public OauthId(String oauthId, OauthType oauthType) {
        this.oauthId = oauthId;
        this.oauthType = oauthType;
    }

}
