package com.nxt.lib.integration;

import com.nxt.lib.integration.api.ApiResponse;
import com.nxt.lib.integration.method.MethodInvocationResult;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Indicate source value for extract operation during the integration process
 * */
@Data
public class ValueSource {

    /**
     * Global input of integration process
     * */
    private final Object input;

    /**
     * Store api call result
     * */
    private final Map<String, ApiResponse> apiCache = new LinkedHashMap<>();

    /**
     * Store method invocation result
     * */
    private final Map<String, MethodInvocationResult> methodCache = new LinkedHashMap<>();

    /**
     * Initialize value source
     * @param input: input of integration process
     * */
    public ValueSource(Object input) {
        this.input = input;
    }

    /**
     * Cache api call result in integration process
     * */
    public void cacheApiResult(String apiName, ApiResponse result) {
        apiCache.put(apiName, result);
    }

    /**
     * Cache method call result in integration process
     * */
    public void cacheMethodResult(String methodName, MethodInvocationResult result) {
        methodCache.put(methodName, result);
    }

    /**
     * Check of method call is already in cache
     * */
    public boolean containsMethod(String methodName) {
        return methodCache.containsKey(methodName);
    }

    /**
     * Check of api call is already in cache
     * */
    public boolean containsApi(String apiName) {
        return apiCache.containsKey(apiName);
    }

    /**
     * Get method result in cache
     * @param methodName: method name
     * */
    public MethodInvocationResult getMethodResult(String methodName) {
        return methodCache.get(methodName);
    }

    /**
     * Get api response in cache
     * @param apiName: api name
     * */
    public ApiResponse getApiResponse(String apiName) {
        return apiCache.get(apiName);
    }
}
