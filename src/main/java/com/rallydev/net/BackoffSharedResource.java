package com.rallydev.net;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class BackoffSharedResource<T> implements SharedResource<T> {
    public static final int BASE_BACKOFF_TIME = 1000 * 10;
    private static final int DEFAULT_BACKOFF_FACTOR = 1;

    private final Ticker ticker;
    private final T resource;

    private final AtomicLong allowUseAfter = new AtomicLong();
    private final AtomicInteger backoffFactor = new AtomicInteger(DEFAULT_BACKOFF_FACTOR);

    public BackoffSharedResource(Ticker ticker, T resource) {
        this.ticker = ticker;
        this.resource = resource;
    }

    @Override
    public <O> O execute(Transaction<T, O> transaction) throws Exception {
        try {
            O result = transaction.with(resource);
            resetBackOffFactor();
            return result;
        } catch (Exception e) {
            recordFailure();
            throw e;
        }
    }

    @Override
    public boolean isAvailable() {
        return allowUseAfter.get() < ticker.read();
    }

    private void resetBackOffFactor() {
        backoffFactor.lazySet(DEFAULT_BACKOFF_FACTOR);
    }

    private void recordFailure() {
        int factor = backoffFactor.getAndIncrement();
        allowUseAfter.set(ticker.read() + (BASE_BACKOFF_TIME * factor));
    }

    @Override
    public String toString() {
        return "BackoffSharedResource{" +
                "resource=" + resource +
                '}';
    }
}
