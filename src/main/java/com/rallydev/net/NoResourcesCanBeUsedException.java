package com.rallydev.net;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class NoResourcesCanBeUsedException extends RuntimeException {
    private final List<Exception> exceptions;

    public NoResourcesCanBeUsedException(List<Exception> exceptions) {
        this.exceptions = exceptions;
    }

    public NoResourcesCanBeUsedException(Exception e) {
        exceptions = new ArrayList<Exception>();
        exceptions.add(e);
    }

    @Override
    public void printStackTrace() {
        super.printStackTrace();
        System.out.println("Total exceptions: " + exceptions.size());

        for(Exception e : exceptions) {
            e.printStackTrace();
        }
    }

    @Override
    public void printStackTrace(PrintStream s) {
        super.printStackTrace(s);
        System.out.println("Total exceptions: " + exceptions.size());

        for(Exception e : exceptions) {
            e.printStackTrace(s);
        }
    }

    @Override
    public void printStackTrace(PrintWriter s) {
        super.printStackTrace(s);
        System.out.println("Total exceptions: " + exceptions.size());

        for(Exception e : exceptions) {
            e.printStackTrace(s);
        }
    }
}
