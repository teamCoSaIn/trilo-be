package com.cosain.trilo.config.security.filter;

import com.cosain.trilo.common.exception.TokenAuthenticationFilterException;
import com.cosain.trilo.config.security.util.SendErrorUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    public ExceptionHandlerFilter(ObjectMapper objectMapper){
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            filterChain.doFilter(request, response);
        } catch (TokenAuthenticationFilterException e){
            log.error(e.getMessage());
            log.error(e.getCause().getMessage());
            SendErrorUtil.sendUnAuthenticationErrorResponse(response, objectMapper, e);
        }

    }

}
