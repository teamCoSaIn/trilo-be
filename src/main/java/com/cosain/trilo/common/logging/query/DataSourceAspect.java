package com.cosain.trilo.common.logging.query;

import java.lang.reflect.Proxy;
import java.sql.Connection;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class DataSourceAspect {

    private final QueryCounter queryCounter;

    public DataSourceAspect(QueryCounter queryCounter) {
        this.queryCounter = queryCounter;
    }

    @Around("execution(* javax.sql.DataSource.getConnection(..))") // 어드바이스 & 포인트컷
    public Connection getConnection(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Connection connection = (Connection) proceedingJoinPoint.proceed();

        return (Connection) Proxy.newProxyInstance(
                connection.getClass().getClassLoader(),
                connection.getClass().getInterfaces(),
                new ConnectionProxyHandler(connection, queryCounter));
    }
}
