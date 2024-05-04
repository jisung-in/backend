package com.jisungin.config;

import com.jisungin.api.oauth.AuthArgumentResolver;
import com.jisungin.api.oauth.AuthInterceptor;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class AuthConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;
    private final AuthArgumentResolver authArgumentResolver;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns(
                        "/v1/{talkRoom}/comments/**",
                        "/v1/comments/**",
                        "/v1/{commentId}/likes/**",
                        "/v1/logout/**",
                        "/v1/reviews/**",
                        "/v1/talk-rooms/**",
                        "/v1/users/**",
                        "/v1/user-libraries/**"

                );
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authArgumentResolver);
    }

}
