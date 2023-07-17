package com.cosain.trilo.auth.presentation;

import com.cosain.trilo.auth.application.token.JwtProvider;
import com.cosain.trilo.common.exception.auth.TokenNotExistException;
import com.cosain.trilo.common.exception.auth.TokenNotValidException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtProvider jwtProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)  {

        if(!(handler instanceof HandlerMethod) || getLoginAnnotation(handler) == null){
            return true;
        }

        if(hasAuthorization(request)){
            validateAuthorization(request);
            return true;
        }

        validateTokenRequired(handler);
        return true;
    }


    private boolean hasAuthorization(HttpServletRequest request){
        return request.getHeader(HttpHeaders.AUTHORIZATION) != null;
    }

    private void validateAuthorization(HttpServletRequest request){
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(!jwtProvider.isValidAccessToken(authorizationHeader)){
            throw new TokenNotValidException();
        }
    }

    private void validateTokenRequired(Object handler) {
        Login loginAnnotation = getLoginAnnotation(handler);
        if(loginAnnotation != null && loginAnnotation.required()){
            throw new TokenNotExistException();
        }
    }

    private Login getLoginAnnotation(Object handler) {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        return handlerMethod.getMethodAnnotation(Login.class);
    }
}
