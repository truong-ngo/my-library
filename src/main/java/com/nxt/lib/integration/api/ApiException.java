package com.nxt.lib.integration.api;

/**
 * A checked exception that wraps an exception thrown by a called api.
 * @see ApiExecutor
 * @author Truong Ngo
 * */
public class ApiException extends Exception {

    /**
     * Hold the exception that throw in api calling process
     * */
    private final Throwable target;

    /**
     * Indicate the request that cause exception
     * */
    private final ApiRequest<Object> request;

    /**
     * Constructs a ApiException with a target exception and a detail message.
     *
     * @param target the target exception
     * @param s      the detail message
     * @param request the request that cause exception
     */
    public ApiException(Throwable target, String s, ApiRequest<Object> request) {
        super(s, null);
        this.target = target;
        this.request = request;
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

    /**
     * Return the request that cause exception
     * @return {@link ApiRequest} object
     * */
    public ApiRequest<Object> getRequest() {
        return request;
    }
}
