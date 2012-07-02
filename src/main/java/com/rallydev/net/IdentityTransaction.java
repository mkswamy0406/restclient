package com.rallydev.net;

public class IdentityTransaction<T> implements Transaction<T, T> {
    @Override
    public T with(T shared) throws Exception {
        return shared;
    }
}
