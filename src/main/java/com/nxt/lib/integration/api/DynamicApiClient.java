package com.nxt.lib.integration.api;

import com.nxt.lib.integration.IntegrationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * Dynamic api client
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
    public static ApiResponse callApi(ApiRequest<Object> request) {
        ResponseEntity<Object> response = null;
        RestClientException exception = null;
        URI uri;
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(request.getUrl());
            if (request.getPathVariables() != null) {
                builder.uriVariables(request.getPathVariables());
            }
            if (request.getQueryParams() != null) {
                request.getQueryParams().forEach((key, values) -> values.forEach(value -> builder.queryParam(key, value)));
            }
            uri = builder.build().toUri();
            HttpHeaders headers = request.getHeaders() != null ? new HttpHeaders(request.getHeaders()) : new HttpHeaders();
            HttpEntity<Object> entity = new HttpEntity<>(request.getBody(), headers);
            response = restTemplate.exchange(uri, HttpMethod.valueOf(request.getMethod()), entity, Object.class);
        } catch (RestClientException e) {
            log.info("Exception occur while calling api: {}", request.getUrl());
            exception = e;
        } catch (IllegalArgumentException e) {
            throw new IntegrationException("Invalid url: " + request.getUrl());
        }
        return new ApiResponse(response, exception);
    }
}
