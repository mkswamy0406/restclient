package com.rallydev.net;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.List;

public class NoResourcesCanBeUsedException extends RuntimeException {
    private final List<Exception> exceptions;

    public NoResourcesCanBeUsedException(List<Exception> exceptions) {
        this.exceptions = exceptions;
    }

    @Override
    public void printStackTrace() {
        super.printStackTrace();

        for(Exception e : exceptions) {
            e.printStackTrace();
        }
    }

    @Override
    public void printStackTrace(PrintStream s) {
        super.printStackTrace(s);

        for(Exception e : exceptions) {
            e.printStackTrace(s);
        }
    }

    @Override
    public void printStackTrace(PrintWriter s) {
        super.printStackTrace(s);

        for(Exception e : exceptions) {
            e.printStackTrace(s);
        }
    }
}
