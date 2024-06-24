package com.nxt.lib.validation.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Validation result
 * */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidationResult {

    /**
     * Is valid result ?
     * */
    private boolean isValid;

    /**
     * Message in case failure
     * */
    private Map<String, Object> messages;
}
