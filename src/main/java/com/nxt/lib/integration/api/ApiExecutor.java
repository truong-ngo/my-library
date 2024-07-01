package com.nxt.lib.integration.api;

import com.nxt.lib.integration.ValueSource;
import com.nxt.lib.integration.utils.IntegrationUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

/**
 * Perform api call and return and {@link ApiResponse} object
 * @author Truong Ngo
 * */
@Slf4j
@AllArgsConstructor
public class ApiExecutor {

    /**
     * Api configuration
     * */
    private final ApiConfiguration configuration;

    /**
     * Call api from given configuration and source
     * <p>
     *     An api calling always return an {@link ApiResponse} object and will
     *     be cache in source
     * </p>
     * @param source source value for extraction in api calling process
     * */
    public ApiResponse performApiCall(ValueSource source) {
        ApiRequest<Object> request = IntegrationUtils.getHttpRequest(configuration, source);
        ResponseEntity<Object> response = null; ApiException exception = null;

        try {
            response = DynamicApiClient.callApi(request);
        } catch (RestClientException e) {
            log.info("An error has occur while calling url: {}", request.getExactUrl());
            exception = new ApiException(e, "An error has occur while calling url: " + request.getExactUrl(), request);
        } catch (IllegalArgumentException e) {
            log.info("Invalid url format: {}", request.getUrl());
            exception = new ApiException(e, "Invalid url format: " + request.getUrl(), request);
        }

        ApiResponse result = new ApiResponse(response, exception);
        source.cacheApiResult(configuration.getApiName(), result);

        return result;
    }
}
