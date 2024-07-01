package com.nxt.lib.integration;

/**
 * Integration exception
 * @author Truong Ngo
 * */
public class IntegrationException extends RuntimeException {

    /**
     * Construct with message
     * */
    public IntegrationException(String message) {
        super(message);
    }

    /**
     * Construct with message, case exception
     * */
    public IntegrationException(String message, Throwable cause) {
        super(message, cause);
    }

}
