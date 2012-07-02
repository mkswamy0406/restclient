package com.rallydev.net;

public class SimpleSharedResource<T> implements SharedResource<T> {
    private T resource;
    private boolean canUse;
    private boolean failureOccurred;

    public SimpleSharedResource(T resource) {
        this(resource, true);
    }

    public SimpleSharedResource(T resource, boolean canUse) {
        this.resource = resource;
        this.canUse = canUse;
    }

    @Override
    public <O> O execute(Transaction<T, O> toTransaction) throws Exception {
        return toTransaction.with(resource);
    }

    @Override
    public boolean isAvailable() {
        return canUse;
    }

    @Override
    public String toString() {
        return "SimpleSharedResource{" +
                "resource=" + resource +
                '}';
    }
}
