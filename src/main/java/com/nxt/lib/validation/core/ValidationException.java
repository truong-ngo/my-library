package com.nxt.lib.validation.core;

import lombok.Getter;

import java.util.Map;

/**
 * Exception throws during the validation process
 * */
@Getter
public class ValidationException extends RuntimeException {

    /**
     * Error message
     * */
    private final Map<String, Object> messages;

    public ValidationException(Map<String, Object> messages) {
        this.messages = messages;
    }

}
