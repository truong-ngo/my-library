package com.nxt.lib.integration.api;

import org.springframework.http.ResponseEntity;

/**
 * Result of call api process
 *
 * @param response  Call api success and get a response
 * @param exception Call api failed with exception
 * @author Truong Ngo
 */
public record ApiResponse(ResponseEntity<Object> response, ApiException exception) {

}
