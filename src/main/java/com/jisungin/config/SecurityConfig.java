package com.jisungin.config;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.security.config.Customizer.withDefaults;

import com.jisungin.infra.security.filter.CustomLogoutFilter;
import com.jisungin.infra.security.oauth.CustomOAuth2UserService;
import com.jisungin.infra.security.oauth.HttpCookieOAuth2AuthorizationRequestRepository;
import com.jisungin.infra.security.oauth.OAuth2SuccessHandler;
import com.jisungin.infra.security.util.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService oAuth2UserService;
    private final OAuth2SuccessHandler successHandler;
    private final CustomAuthenticationEntryPoint entryPoint;
    private final CustomLogoutFilter logoutFilter;
    private final HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .formLogin(formLogin -> formLogin.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .csrf(csrf -> csrf.disable())
                .cors(withDefaults())
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))
                .authorizeHttpRequests(request ->
                        request.requestMatchers(PathRequest.toH2Console()).permitAll()
                                .requestMatchers("/v1/books/**").permitAll()
                                .requestMatchers("/v1/search/rank").permitAll()
                                .requestMatchers(GET, "v1/talk-rooms").permitAll()
                                .requestMatchers(GET, "v1/talk-room/{talkRoomId}").permitAll()
                                .requestMatchers(GET, "v1/{talkRoomId}/comments").permitAll()
                                .requestMatchers(GET, "/v1/user-libraries").permitAll()
                                .requestMatchers("/v1/oauth2/**").permitAll()
                                .anyRequest().authenticated())
                .exceptionHandling(exceptionHandlingConfigurer ->
                        exceptionHandlingConfigurer.authenticationEntryPoint(entryPoint))
                .oauth2Login(oAuth2LoginConfigurer ->
                        oAuth2LoginConfigurer
                                .authorizationEndpoint(authorizationEndpointConfig ->
                                        authorizationEndpointConfig.baseUri("/v1/oauth2/authorization")
                                                .authorizationRequestRepository(authorizationRequestRepository))
                                .redirectionEndpoint(redirectionEndpointConfig ->
                                        redirectionEndpointConfig.baseUri("/login/oauth2/code/*"))
                                .userInfoEndpoint(userInfoEndpointConfig ->
                                        userInfoEndpointConfig.userService(oAuth2UserService))
                                .successHandler(successHandler))
                .addFilterBefore(logoutFilter, LogoutFilter.class);

        return http.build();
    }

}
