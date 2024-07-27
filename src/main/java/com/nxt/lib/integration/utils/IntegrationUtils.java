package com.nxt.lib.integration.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nxt.lib.integration.IntegrationException;
import com.nxt.lib.integration.ValueSource;
import com.nxt.lib.integration.api.ApiConfiguration;
import com.nxt.lib.integration.api.ApiRequest;
import com.nxt.lib.utils.ClassUtils;
import com.nxt.lib.utils.IOUtils;
import com.nxt.lib.utils.SpElUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
     * Get configuration from file path
     * @param path path to file
     * @param clazz desire type
     * @return configuration of type {@code T}
     * @throws IntegrationException if error occur
     * */
    public static <T> T getConfiguration(String path, Class<T> clazz) {
        return IOUtils.getResource(path, clazz)
                .orElseThrow(() -> new IntegrationException("Invalid flow: " + path));
    }

    /**
     * Get configuration from file path
     * @param path path to file
     * @param type desire generic type ({@code java.util.List}, {@code java.util.Map}...)
     * @return configuration of type {@code T}
     * @throws IntegrationException if path is invalid or configuration is invalid
     * */
    public static <T> T getConfiguration(String path, TypeReference<T> type) {
        return IOUtils.getResource(path, type)
                .orElseThrow(() -> new IntegrationException("Invalid flow: " + path));
    }

    /**
     * Extract value base on SpEl expression from given context
     * @param expression: SpEl expression as extractor
     * @param context: context for extraction
     * @return value extracted as {@link Object} type
     * @throws IntegrationException if path is invalid or configuration is invalid
     * */
    public static Object extractValue(String expression, Object context) {
        return SpElUtils.getValue(expression, context)
                .orElseThrow(() -> new IntegrationException("Invalid expression string: " + expression));

    }

    /**
     * Extract value base on SpEl expression from given context
     * @param expression: SpEl expression as extractor
     * @param context: context for extraction
     * @param clazz: desired type
     * @return value extracted as {@link T} type
     * @throws IntegrationException if error occur in expression parsing process
     * */
    public static <T> T extractValue(String expression, Object context, Class<T> clazz) {
        return SpElUtils.getValue(expression, context, clazz)
                .orElseThrow(() -> new IntegrationException("Invalid expression string: " + expression));
    }

    /**
     * Extract condition that decide if the operation going to be executed
     * @param invokeCondition: expression that indicate condition
     * @param source: context to extraction
     * @return true if invokeCondition is null or the extract value is true otherwise false
     * */
    public static Boolean getCondition(String invokeCondition, ValueSource source) {
        return Objects.isNull(invokeCondition) || extractValue(invokeCondition, source, Boolean.class);
    }

    /**
     * Get value that define in configuration
     * @param configuration: the configuration for value of object
     * @return object value
     * */
    @SuppressWarnings("all")
    public static Object getValueFromConfig(Object configuration, ValueSource source) {
        if (Objects.isNull(configuration)) {
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
        if (Objects.nonNull(configuration.getHeaders())) {
            configuration.getHeaders().forEach((k, v) -> headers.put(k, List.of((String) extractValue(v, sources))));
        }

        MultiValueMap<String, Object> queryParams = new LinkedMultiValueMap<>();
        if (Objects.nonNull(configuration.getQueryParams())) {
            configuration.getQueryParams().forEach((k, v) -> queryParams.put(k, List.of(extractValue(v, sources))));
        }

        Map<String, Object> pathVariables = new HashMap<>();
        if (Objects.nonNull(configuration.getPathVariables())) {
            configuration.getPathVariables().forEach((k, v) -> pathVariables.put(k, extractValue(v, sources)));
        }

        Object body = getValueFromConfig(configuration.getBody(), sources);

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
