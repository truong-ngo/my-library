package com.nxt.lib.integration;

import com.nxt.lib.integration.api.ApiResponse;
import com.nxt.lib.integration.method.MethodInvocationResult;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Indicate source value for extraction during the integration process
 * @author Truong Ngo
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
     * @param apiName: name of api
     * @param result: result of api call process
     * */
    public void cacheApiResult(String apiName, ApiResponse result) {
        apiCache.put(apiName, result);
    }

    /**
     * Cache method call result in integration process
     * @param methodSignature: method unique identity (signature)
     * @param result: method invocation result
     * */
    public void cacheMethodResult(String methodSignature, MethodInvocationResult result) {
        methodCache.put(methodSignature, result);
    }

    /**
     * Check of method call is already in cache
     * @param methodSignature: method identifier
     * */
    public boolean containsMethod(String methodSignature) {
        return methodCache.containsKey(methodSignature);
    }

    /**
     * Check of api call is already in cache
     * @param apiName: api unique name
     * */
    public boolean containsApi(String apiName) {
        return apiCache.containsKey(apiName);
    }

    /**
     * Get method result in cache
     * @param methodSignature: method unique identifier
     * */
    public MethodInvocationResult getMethodResult(String methodSignature) {
        return methodCache.get(methodSignature);
    }

    /**
     * Get api response in cache
     * @param apiName: api unique name
     * */
    public ApiResponse getApiResponse(String apiName) {
        return apiCache.get(apiName);
    }
}
