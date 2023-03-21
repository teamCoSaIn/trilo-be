package com.cosain.trilo.config.security.util;

import com.cosain.trilo.common.dto.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;

import java.io.IOException;

public class SendErrorUtil {

    public static void sendUnAuthenticationErrorResponse(HttpServletResponse response){
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
    }

    public static void sendUnAuthenticationErrorResponse(HttpServletResponse response, ObjectMapper objectMapper, Throwable e) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        String errorMessage = getCauseErrorMessage(e);

        ErrorResponse dto = ErrorResponse.from(errorMessage);
        objectMapper.writeValue(response.getWriter(), dto);
    }

    private static String getCauseErrorMessage(Throwable e){
        return e.getCause() == null ? e.getMessage() : e.getCause().getMessage();
    }
}
