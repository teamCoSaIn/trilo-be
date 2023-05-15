package com.cosain.trilo.common.logging.query;

import org.springframework.stereotype.Component;


@Component
public class QueryCounter {

    private final ThreadLocal<Integer> count = ThreadLocal.withInitial(() -> 0);

    public void increaseCount() {
        count.set(count.get() + 1);
    }

    public int getCount() {
        return count.get();
    }

    public void resetCount() {
        count.set(0);
    }
}
