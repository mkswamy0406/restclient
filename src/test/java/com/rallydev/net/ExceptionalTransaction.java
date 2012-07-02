package com.rallydev.net;

public class ExceptionalTransaction<T> implements  Transaction<T, T> {
    private boolean hasBeenInvoked = false;
    @Override
    public T with(T shared) throws Exception {
        if(hasBeenInvoked) return shared;
        hasBeenInvoked = true;

        throw new Exception();
    }
}
