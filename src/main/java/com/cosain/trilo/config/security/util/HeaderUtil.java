package com.cosain.trilo.config.security.util;

import com.cosain.trilo.common.exception.AuthorizationHeaderTypeException;
import jakarta.servlet.http.HttpServletRequest;

public class HeaderUtil {
    private final static String HEADER_AUTHORIZATION = "Authorization";
    private final static String TOKEN_AUTH_TYPE = "Bearer";
    public static String getAuthTokenFrom(HttpServletRequest request){
        String headerValue = request.getHeader(HEADER_AUTHORIZATION);
        if(!headerValue.equals(TOKEN_AUTH_TYPE)) throw new AuthorizationHeaderTypeException();

        return headerValue.substring(TOKEN_AUTH_TYPE.length());
    }

    public static boolean isAuthorizationHeaderExist(HttpServletRequest request){
        return request.getHeader(HEADER_AUTHORIZATION) != null;
    }
}
