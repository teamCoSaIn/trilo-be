package com.cosain.trilo.common.logging.query;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DataSourceAspectTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void testDataSourceAspect() throws SQLException {
        // given
        Object ret = dataSource.getConnection();

        // when & then
        assertThat(ret).isNotNull();
        assertThat(ret).isInstanceOf(Connection.class);
    }
}
