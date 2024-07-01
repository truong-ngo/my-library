package com.nxt.lib.integration.api;

import lombok.Builder;
import lombok.Data;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

/**
 * Represent Http request
 * @author Truong Ngo
 * */
@Data
@Builder
public class ApiRequest<T> {

    /**
     * A string represent form of url
     * <p>
     *     Example: localhost:8080/{pathVariable}?param={value}
     * </p>
     * */
    private String url;

    /**
     * Headers value
     * */
    private MultiValueMap<String, String> headers;

    /**
     * Query params value
     * */
    private MultiValueMap<String, Object> queryParams;

    /**
     * Path variables value
     * */
    private Map<String, Object> pathVariables;

    /**
     * Http method name
     * */
    private String method;

    /**
     * Body value
     * */
    private T body;

    /**
     * Get exactly url from url form, query params, path variables
     * @return the exact url string
     * */
    public String getExactUrl() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        if (pathVariables != null) {
            builder.uriVariables(pathVariables);
        }
        if (queryParams != null) {
            queryParams.forEach((key, values) -> values.forEach(value -> builder.queryParam(key, value)));
        }
        return builder.build().toUri().toString();
    }
}
