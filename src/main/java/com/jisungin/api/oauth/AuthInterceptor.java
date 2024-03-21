package com.jisungin.api.oauth;

import com.jisungin.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.PathMatcher;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.jisungin.api.oauth.AuthConstant.*;
import static com.jisungin.exception.ErrorCode.UNAUTHORIZED_REQUEST;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final AuthContext authContext;
    private final ObjectProvider<PathMatcher> pathMatcherProvider;
    private final Set<UriAndMethodsCondition> authNotRequiredConditions = new HashSet<>();

    public void setAuthNotRequiredConditions(UriAndMethodsCondition... authNotRequiredConditions) {
        this.authNotRequiredConditions.clear();
        this.authNotRequiredConditions.addAll(Arrays.asList(authNotRequiredConditions));
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("시작");
        if (CorsUtils.isPreFlightRequest(request)) {
            return true;
        }

        if (isAuthenticationNotRequired(request)) {
            return true;
        }

        HttpSession session = getSession(request);
        Long userId = Optional.ofNullable(session.getAttribute(JSESSION_ID))
                .map(id -> (Long) id)
                .orElseThrow(() -> new BusinessException(UNAUTHORIZED_REQUEST));
        authContext.setUserId(userId);
        return true;
    }

    private boolean isAuthenticationNotRequired(HttpServletRequest request) {
        HttpMethod httpMethod = HttpMethod.valueOf(request.getMethod());
        String requestURI = request.getRequestURI();
        PathMatcher pathMatcher = pathMatcherProvider.getIfAvailable();
        return authNotRequiredConditions.stream()
                .anyMatch(it -> it.match(pathMatcher, requestURI, httpMethod));
    }

    private HttpSession getSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null) {
            // TODO. 추후에 인증과 관련된 예외처리를 적용할 예정
            throw new BusinessException(UNAUTHORIZED_REQUEST);
        }
        return session;
    }

    public record UriAndMethodsCondition(String uriPattern, Set<HttpMethod> httpMethods) {
        public boolean match(PathMatcher pathMatcher, String requestURI, HttpMethod httpMethod) {
            return pathMatcher.match(uriPattern, requestURI) && httpMethods.contains(httpMethod);
        }
    }

}
