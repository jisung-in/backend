package com.jisungin.api.oauth;

import com.jisungin.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import static com.jisungin.exception.ErrorCode.UNAUTHORIZED_REQUEST;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthArgumentResolver implements HandlerMethodArgumentResolver {

    private final AuthContext authContext;

    // 요청을 했을 때, @Auth와 내부 변수 타입이 Long인지 확인한다.
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Auth.class)
                && parameter.getParameterType().equals(Long.class);
    }

    // userId를 확인하고 해당 값을 제공한다.
    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) throws Exception {
        if (authContext.getUserId() == null) {
            // TODO. 추후에 인증과 관련된 예외처리를 적용할 예정
            throw new BusinessException(UNAUTHORIZED_REQUEST);
        }
        return authContext.getUserId();
    }

}
