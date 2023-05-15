package com.cosain.trilo.common.logging.query;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PreparedStatementProxyHandlerTest {

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private QueryCounter queryCounter;

    private PreparedStatementProxyHandler proxyHandler;

    @BeforeEach
    public void setup() {
        proxyHandler = new PreparedStatementProxyHandler(preparedStatement, queryCounter);
    }

    @Test
    @DisplayName("execute 문자열이 포함된 메서드가 실행될 경우 쿼리 카운터의 증가 메서드가 호출된다 - executeQuery()")
    public void testInvoke_executeQuery_increasesQueryCount() throws Throwable {
        Method executeQueryMethod = PreparedStatement.class.getMethod("executeQuery");
        given(preparedStatement.executeQuery()).willReturn(mock(ResultSet.class));

        proxyHandler.invoke(preparedStatement, executeQueryMethod, null);

        verify(queryCounter, times(1)).increaseCount();
    }

    @Test
    @DisplayName("execute 문자열이 포함된 메서드가 실행될 경우 쿼리 카운터의 증가 메서드가 호출된다 - executeUpdate()")
    public void testInvoke_excuteUpdate_increasesQueryCount() throws Throwable {
        Method executeQueryMethod = PreparedStatement.class.getMethod("executeUpdate");
        given(preparedStatement.executeUpdate()).willReturn(1);

        proxyHandler.invoke(preparedStatement, executeQueryMethod, null);
        proxyHandler.invoke(preparedStatement, executeQueryMethod, null);

        verify(queryCounter, times(2)).increaseCount();
    }

    @Test
    @DisplayName("execute 문자열이 포함된 메서드가 실행될 경우 쿼리 카운터의 증가 메서드가 호출된다 - executeLargeUpdate")
    public void testInvoke_executeLargeUpdate_increasesQueryCount() throws Throwable {
        Method executeQueryMethod = PreparedStatement.class.getMethod("executeLargeUpdate");
        given(preparedStatement.executeLargeUpdate()).willReturn(1L);

        proxyHandler.invoke(preparedStatement, executeQueryMethod, null);
        proxyHandler.invoke(preparedStatement, executeQueryMethod, null);
        proxyHandler.invoke(preparedStatement, executeQueryMethod, null);

        verify(queryCounter, times(3)).increaseCount();
    }


}
