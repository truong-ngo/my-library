package com.nxt.lib.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nxt.lib.integration.api.ApiFlowConfiguration;
import com.nxt.lib.integration.api.ApiFlowExecutor;
import com.nxt.lib.integration.api.ApiResponse;
import com.nxt.lib.validation.core.RuleConfiguration;
import com.nxt.lib.validation.core.ValidationException;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.util.Map;

/**
 * Integration process executor
 * */
public class IntegrationExecutor {

    /**
     * Value source
     * */
    private final ValueSource source;

    public IntegrationExecutor(Object input) {
        this.source = new ValueSource(input);
    }

    /**
     * Execute integration flow
     * @param configuration: flow configuration
     * */
    public ApiResponse execute(ApiFlowConfiguration configuration) {
        ApiFlowExecutor apiFlowExecutor = new ApiFlowExecutor(configuration);
        return apiFlowExecutor.executeApiFlow(source);
    }

    /**
     * Get api flow configuration from flow file name
     * */
    public static ApiFlowConfiguration getApiFlowConfiguration(String path) {
        ClassPathResource resource = new ClassPathResource(path);
        try (InputStream in = resource.getInputStream()) {
            return new ObjectMapper().readValue(in.readAllBytes(), ApiFlowConfiguration.class);
        } catch (Exception e) {
            throw new IntegrationException("Invalid flow: " + path);
        }
    }
}
