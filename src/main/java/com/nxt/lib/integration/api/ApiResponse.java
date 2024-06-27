package com.nxt.lib.integration.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

/**
 * Result of call api process
 *
 * @param response  Call api success and get a response
 * @param exception Call api failed with exception
 */
public record ApiResponse(ResponseEntity<Object> response, RestClientException exception) {

}
