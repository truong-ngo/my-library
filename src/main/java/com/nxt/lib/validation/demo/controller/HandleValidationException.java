package com.nxt.lib.validation.demo.controller;

import com.nxt.lib.validation.core.ValidationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class HandleValidationException {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(ValidationException exception) {
        return ResponseEntity.ok(exception.getMessages());
    }
}
