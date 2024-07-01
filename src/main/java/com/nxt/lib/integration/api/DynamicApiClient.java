package com.nxt.lib.integration.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

/**
 * Dynamic api client
 * @author Truong Ngo
 * */
@Slf4j
public class DynamicApiClient {

    /**
     * Prevent instantiate
     * */
    private DynamicApiClient() {}

    private static final RestTemplate restTemplate = new RestTemplate();

    /**
     * Call external api
     * @param request: api request (body, headers, ...)
     * @return {@link ApiResponse} object
     * */
    public static ResponseEntity<Object> callApi(ApiRequest<Object> request) {
        String url = request.getExactUrl();
        HttpHeaders headers = request.getHeaders() != null ? new HttpHeaders(request.getHeaders()) : new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<Object> entity = new HttpEntity<>(request.getBody(), headers);
        return restTemplate.exchange(url, HttpMethod.valueOf(request.getMethod()), entity, Object.class);
    }
}
