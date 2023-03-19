package com.cosain.trilo.config.security.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.util.concurrent.TimeUnit;

public class CookieUtil {

    private static final String AUTH_COOKIE_NAME = "refreshToken";

    public static void addAuthCookie(HttpServletResponse response, String refreshToken, Long tokenExpiry){
        int maxAge = (int) TimeUnit.MICROSECONDS.toSeconds(tokenExpiry);
        Cookie cookie = new Cookie(AUTH_COOKIE_NAME, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);

    }
}
