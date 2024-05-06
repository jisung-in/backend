package com.jisungin.infra.security.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jisungin.api.ApiResponse;
import com.nimbusds.jose.util.StandardCharset;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper om;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding(StandardCharset.UTF_8.name());
        response.getWriter().write(om.writeValueAsString(createApiResponse()));
        response.getWriter().flush();
    }

    private ApiResponse<Object> createApiResponse() {
        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("로그인 성공")
                .build();
    }

}
