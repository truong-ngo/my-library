package com.nxt.lib.validation.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Validation result
 * @author Truong Ngo
 * */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidationResult {

    /**
     * Valid result of validation process
     * */
    public static final ValidationResult VALID_RESULT = new ValidationResult(true, null);

    /**
     * Indicate that the validation result is success or failed
     * */
    private boolean isValid;

    /**
     * Message in case failure
     * <p>
     * Contain the all the field is invalid during the validation process
     * */
    private Map<String, Object> messages;
}
