package com.nxt.lib.integration.api;

import lombok.Builder;
import lombok.Data;
import org.springframework.util.MultiValueMap;

import java.util.Map;

/**
 * Represent Http request
 * */
@Data
@Builder
public class ApiRequest<T> {
    private String url;
    private MultiValueMap<String, String> headers;
    private MultiValueMap<String, Object> queryParams;
    private Map<String, Object> pathVariables;
    private String method;
    private T body;
}
