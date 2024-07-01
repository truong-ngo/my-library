package com.nxt.lib.integration.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nxt.lib.integration.IntegrationException;
import com.nxt.lib.integration.ValueSource;
import com.nxt.lib.integration.api.ApiConfiguration;
import com.nxt.lib.integration.api.ApiRequest;
import com.nxt.lib.utils.ClassUtils;
import com.nxt.lib.utils.IOUtils;
import com.nxt.lib.utils.SpElUtils;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.ParseException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Integration utility
 * @author Truong Ngo
 * */
public class IntegrationUtils {

    /**
     * Prevent instantiate
     * */
    private IntegrationUtils() {}

    /**
     * Get api flow configuration from flow file name
     * */
    public static <T> T getConfiguration(String path, Class<T> clazz) {
        try {
            return IOUtils.getResource(path, clazz);
        } catch (Exception e) {
            throw new IntegrationException("Invalid flow: " + path);
        }
    }

    /**
     * Get api flow configuration from flow file name
     * */
    public static <T> T getConfiguration(String path, TypeReference<T> type) {
        try {
            return IOUtils.getResource(path, type);
        } catch (Exception e) {
            throw new IntegrationException("Invalid flow: " + path);
        }
    }

    /**
     * Extract value base on SpEl expression from given context
     * @param expression: SpEl expression as extractor
     * @param context: context for extraction
     * @return value extracted as {@link Object} type
     * */
    public static Object extractValue(String expression, Object context) {
        try {
            return SpElUtils.getValue(expression, context);
        } catch (ParseException | EvaluationException | IllegalAccessError exception) {
            throw new IntegrationException("Invalid expression string: " + expression, exception);
        }
    }

    /**
     * Extract value base on SpEl expression from given context
     * @param expression: SpEl expression as extractor
     * @param context: context for extraction
     * @param clazz: desired type
     * @return value extracted as {@link T} type
     * */
    public static <T> T extractValue(String expression, Object context, Class<T> clazz) {
        try {
            return SpElUtils.getValue(expression, context, clazz);
        } catch (ParseException | EvaluationException | IllegalAccessError exception) {
            throw new IntegrationException("Invalid expression string: " + expression, exception);
        }
    }

    /**
     * Extract condition that decide what next method / api is going to be executed
     * @param invokeCondition: expression that indicate condition
     * @param source: context to extraction
     * */
    public static Boolean getCondition(String invokeCondition, ValueSource source) {
        if (invokeCondition == null) {
            return true;
        }
        return extractValue(invokeCondition, source, Boolean.class);
    }

    /**
     * Get value that define in configuration
     * @param configuration: the configuration for value of object
     * @return object value
     * */
    public static Object getValueFromConfig(Object configuration, ValueSource source) {
        if (configuration == null) {
            return null;
        }
        if (ClassUtils.isPrimitive(configuration) || ClassUtils.isWrapper(configuration)) {
            return configuration;
        }
        if (configuration instanceof String string) {
            return extractValue(string, source);
        }
        if (configuration instanceof Map<?,?> map) {
            return map.entrySet().stream().collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> getValueFromConfig(entry.getValue(), source)
            ));
        }
        if (configuration instanceof List<?> list) {
            return list.stream().map(l -> getValueFromConfig(l, source));
        }
        return null;
    }

    /**
     * Get {@link ApiRequest} instance from {@link ApiConfiguration}
     * @param configuration: an {@link ApiConfiguration}
     * @return and {@link ApiRequest} object
     * */
    public static ApiRequest<Object> getHttpRequest(ApiConfiguration configuration, ValueSource sources) {

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

        Object body = IntegrationUtils.getValueFromConfig(configuration.getBody(), sources);

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
