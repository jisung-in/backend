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
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    private final ObjectMapper om;


    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        response.setContentType("application/json");
        response.setStatus(SC_OK);
        response.setCharacterEncoding(UTF_8.name());
        response.getWriter().write(om.writeValueAsString(createApiResponse()));
        response.getWriter().flush();
    }

    private ApiResponse<Object> createApiResponse() {
        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("로그아웃 성공")
                .build();
    }

}
