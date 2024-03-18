package com.jisungin.api.oauth;

import com.jisungin.domain.oauth.OauthType;
import org.springframework.core.convert.converter.Converter;

public class OauthTypeConverter implements Converter<String, OauthType> {

    @Override
    public OauthType convert(String source) {
        return OauthType.fromName(source);
    }

}
