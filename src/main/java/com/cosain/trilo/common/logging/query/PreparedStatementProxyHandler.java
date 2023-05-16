package com.cosain.trilo.common.logging.query;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;

public class PreparedStatementProxyHandler implements InvocationHandler {

    private final PreparedStatement preparedStatement; // target
    private final QueryCounter queryCounter;

    public PreparedStatementProxyHandler(PreparedStatement preparedStatement, QueryCounter queryCounter) {
        this.preparedStatement = preparedStatement;
        this.queryCounter = queryCounter;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (isExecuteQuery(method)) { // 조건에 따라 부가 기능 실행 : 쿼리 개수 ++
            queryCounter.increaseCount();
        }
        return method.invoke(preparedStatement, args); // target 으로 위임하고 결과 반환
    }

    private boolean isExecuteQuery(Method method) {
        String methodName = method.getName();
        return methodName.contains("execute");
    }
}