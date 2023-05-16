package com.cosain.trilo.common.logging.query;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class ConnectionProxyHandler implements InvocationHandler {

    private final Connection connection; // target
    private final QueryCounter queryCounter;

    public ConnectionProxyHandler(Connection connection, QueryCounter queryCounter) {
        this.connection = connection;
        this.queryCounter = queryCounter;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object ret = method.invoke(connection, args); // 타겟(Connection)으로 위임하고 결과 받음

        // 메소드 선별 부가기능 적용
        if (ret instanceof PreparedStatement && method.getName().equals("prepareStatement")) {
            // PreparedStatement 동적 프록시 생성
            return  Proxy.newProxyInstance(
                    ret.getClass().getClassLoader(),
                    ret.getClass().getInterfaces(),
                    new PreparedStatementProxyHandler((PreparedStatement) ret, queryCounter)
            );
        }else return ret; // 결과 반환
    }

}