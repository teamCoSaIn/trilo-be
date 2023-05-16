package com.cosain.trilo.common.logging.query;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class QueryCountInterceptor implements HandlerInterceptor {

    private static final String QUERY_INFO_FORMAT = "QUERY_INFO : [{} {}] [STATUS CODE: {}] [QUERY_COUNT: {}]";
    private final QueryCounter queryCounter;

    public QueryCountInterceptor(QueryCounter queryCounter) {
        this.queryCounter = queryCounter;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {

        int count = queryCounter.getCount();
        int status = response.getStatus();
        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        log.info(QUERY_INFO_FORMAT, method, requestURI, status, count);
        queryCounter.resetCount();
    }
}
