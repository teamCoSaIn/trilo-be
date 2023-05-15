package com.cosain.trilo.common.logging.query;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ConnectionProxyHandlerTest {

    @Mock
    private Connection connection;
    @Mock
    private QueryCounter queryCounter;
    @Mock
    private PreparedStatement preparedStatement;

    @Test
    @DisplayName("invoke() 실행 시 Proxy & PreparedStatement 타입의 객체가 반환된다")
    void testInvoke_PreparedStatementProxy_Returned() throws Throwable{

        // given
        given(connection.prepareStatement(anyString())).willReturn(preparedStatement);
        ConnectionProxyHandler connectionProxyHandler = new ConnectionProxyHandler(connection, queryCounter);
        Method method = Connection.class.getMethod("prepareStatement", String.class);

        // when
        Object invoke = connectionProxyHandler.invoke(connection, method, new String[]{""});

        // then
        assertInstanceOf(PreparedStatement.class, invoke);
        assertInstanceOf(Proxy.class, invoke);
    }

}
