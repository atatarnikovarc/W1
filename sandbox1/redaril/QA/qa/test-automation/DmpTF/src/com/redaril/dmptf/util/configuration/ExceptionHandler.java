package com.redaril.dmptf.util.configuration;

public class ExceptionHandler {
    private ExceptionHandler() {
    }

    public static void castToRuntime(Throwable t) {
        if (t instanceof RuntimeException) {
            throw (RuntimeException) t;
        } else {
            RuntimeException ex = new RuntimeException();

            ex.setStackTrace(t.getStackTrace());
            ex.initCause(t);
            throw ex;
        }
    }

    public static void castToRuntime(String message, Throwable t) {
        RuntimeException ex = new RuntimeException(message);

        ex.setStackTrace(t.getStackTrace());
        ex.initCause(t);
        throw ex;
    }
}
