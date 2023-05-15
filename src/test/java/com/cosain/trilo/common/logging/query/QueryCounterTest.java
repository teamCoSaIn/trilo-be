package com.cosain.trilo.common.logging.query;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class QueryCounterTest {

    private QueryCounter queryCounter;

    @BeforeEach
    public void setUp() {
        queryCounter = new QueryCounter();
    }

    @Test
    @DisplayName("쿼리 개수 증가")
    public void testIncreaseCount(){

        queryCounter.increaseCount();
        queryCounter.increaseCount();
        queryCounter.increaseCount();
        int count = queryCounter.getCount();

        assertThat(count).isEqualTo(3);
    }

    @Test
    @DisplayName("쿼리 개수 초기화")
    public void testReset(){

        queryCounter.increaseCount();
        queryCounter.increaseCount();

        queryCounter.resetCount();

        int count = queryCounter.getCount();

        assertThat(count).isEqualTo(0);
    }
}
