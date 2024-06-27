package com.nxt.lib.integration.api;

import com.nxt.lib.integration.ValueSource;
import com.nxt.lib.integration.method.MethodConfiguration;
import com.nxt.lib.integration.method.MethodExecutor;
import com.nxt.lib.integration.utils.IntegrationUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Execute a api call flow
 * */
public class ApiFlowExecutor {

    /**
     * Step configuration
     * */
    private final ApiFlowConfiguration configuration;

    public ApiFlowExecutor(ApiFlowConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * Execute a step of api integration
     * @param source: value source
     * @return {@link ApiResponse} instance
     * */
    public ApiResponse executeApiFlow(ValueSource source) {

        if (configuration.getPreCall() != null) {
            if (IntegrationUtils.extractCondition(configuration.getPreCall().getInvokeCondition(), source)) {
                MethodExecutor preCallExecutor = new MethodExecutor(configuration.getPreCall());
                preCallExecutor.invokeMethod(source);
            }
        }

        ApiRequest<Object> request = toHttpRequest(configuration.getApiConfig(), source);
        ApiResponse response = DynamicApiClient.callApi(request);
        source.cacheApiResult(configuration.getApiConfig().getApiName(), response);

        if (configuration.getResponseHandlers() != null && !configuration.getResponseHandlers().isEmpty()) {
            for (MethodConfiguration handler : configuration.getResponseHandlers()) {
                if (IntegrationUtils.extractCondition(handler.getInvokeCondition(), source)) {
                    MethodExecutor handlerExecutor = new MethodExecutor(handler);
                    handlerExecutor.invokeMethod(source);
                }
            }
        }

        if (configuration.getNextStep() == null || configuration.getNextStep().isEmpty()) {
            return response;
        }

        for (ApiFlowConfiguration nextStep : configuration.getNextStep()) {
            if (IntegrationUtils.extractCondition(nextStep.getCallCondition(), source)) {
                ApiFlowExecutor nextStepExecutor = new ApiFlowExecutor(nextStep);
                return nextStepExecutor.executeApiFlow(source);
            }
        }

        return response;
    }

    /**
     * Get {@link ApiRequest} instance from {@link ApiConfiguration}
     * */
    public ApiRequest<Object> toHttpRequest(ApiConfiguration configuration, ValueSource sources) {

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        if (configuration.getHeaders() != null) {
            configuration.getHeaders().forEach((k, v) -> headers.put(k, List.of((String) IntegrationUtils.extractValue(v, sources))));
        }

        MultiValueMap<String, Object> queryParams = new LinkedMultiValueMap<>();
        if (configuration.getQueryParams() != null) {
            configuration.getQueryParams().forEach((k, v) -> queryParams.put(k, List.of(IntegrationUtils.extractValue(v, sources))));
        }

        Map<String, Object> pathVariables = new HashMap<>();
        if (configuration.getPathVariables() != null) {
            configuration.getPathVariables().forEach((k, v) -> pathVariables.put(k, IntegrationUtils.extractValue(v, sources)));
        }

        Map<String, Object> body = new HashMap<>();
        if (configuration.getBody() != null) {
            configuration.getBody().forEach((k, v) -> body.put(k, IntegrationUtils.extractValue(v, sources)));
        }

        return ApiRequest.builder()
                .url(configuration.getUrl())
                .method(configuration.getMethod())
                .headers(headers)
                .queryParams(queryParams)
                .pathVariables(pathVariables)
                .body(body)
                .build();
    }
}
