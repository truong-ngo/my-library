package com.nxt.lib.integration.method;

/**
 * A checked exception that wraps an exception thrown by an invoked method.
 * @see MethodExecutor
 * @author Truong Ngo
 * */
public class MethodInvocationException extends Exception {

    /**
     * Hold the exception that throw in method invocation
     * */
    private final Throwable target;

    /**
     * Constructs a MethodInvocationException with a target exception and a detail message.
     *
     * @param target the target exception
     * @param s      the detail message
     */
    public MethodInvocationException(Throwable target, String s) {
        super(s, null);  // Disallow initCause
        this.target = target;
    }

    /**
     * Returns the cause of this exception (the thrown target exception,
     * which may be {@code null}).
     *
     * @return  the cause of this exception.
     */
    @Override
    public Throwable getCause() {
        return target;
    }
}
