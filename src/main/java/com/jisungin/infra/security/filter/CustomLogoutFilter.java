package com.jisungin.infra.security.filter;

import static com.nimbusds.jose.util.StandardCharset.UTF_8;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jisungin.api.ApiResponse;
import com.jisungin.infra.security.util.CookieUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class CustomLogoutFilter extends OncePerRequestFilter {

    private final RequestMatcher logoutRequestMatcher;
    private final ObjectMapper om;

    public CustomLogoutFilter(ObjectMapper om) {
        this.logoutRequestMatcher = new AntPathRequestMatcher("/v1/logout", "POST");
        this.om = om;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (requiresLogout(request)) {
            invalidateSession(request);
            deleteSessionCookie(request, response);
            writeApiResponse(response);

            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean requiresLogout(HttpServletRequest request) {
        return this.logoutRequestMatcher.matches(request);
    }

    private void invalidateSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) session.invalidate();
    }

    private void deleteSessionCookie(HttpServletRequest request, HttpServletResponse response) {
        CookieUtils.deleteCookie(request, response, "JSESSIONID");
    }

    private void writeApiResponse(HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setStatus(SC_OK);
        response.setCharacterEncoding(UTF_8.name());
        response.getWriter().write(om.writeValueAsString(buildApiResponse()));
        response.getWriter().flush();
    }

    private ApiResponse<Object> buildApiResponse() {
        return ApiResponse.builder()
                .status(HttpStatus.OK)
                .message("로그아웃 성공")
                .build();
    }

}
