package com.jisungin.infra.security.util;

import static com.nimbusds.jose.util.StandardCharset.*;
import static jakarta.servlet.http.HttpServletResponse.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jisungin.api.ApiResponse;
import com.nimbusds.jose.util.StandardCharset;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper om;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        response.setContentType("application/json");
        response.setStatus(SC_UNAUTHORIZED);
        response.setCharacterEncoding(UTF_8.name());
        response.getWriter().write(om.writeValueAsString(createApiResponse()));
        response.getWriter().flush();
    }

    private ApiResponse<Object> createApiResponse() {
        return ApiResponse.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .message("인증되지 않은 사용자 입니다.")
                .build();
    }

}
