package com.nxt.lib.integration;

import com.nxt.lib.integration.api.ApiConfiguration;
import com.nxt.lib.integration.method.MethodConfiguration;
import lombok.Data;

import java.util.List;

/**
 * Define a complete process include:
 * <ul>
 *   <li>Pre api call handler</li>
 *   <li>Api calling</li>
 *   <li>Response handlers</li>
 * </ul>
 * @see ApiConfiguration
 * @see MethodConfiguration
 * @author Truong Ngo
 * */
@Data
public class ProcessConfiguration implements OperationalConfiguration {

    /**
     * Process name
     * */
    private String processName;

    /**
     * Invoke condition of a process
     * */
    private String invokeCondition;

    /**
     * Pre api call handler's configuration
     * */
    private List<List<MethodConfiguration>> preApiCallHandlers;

    /**
     * Api configuration
     * */
    private ApiConfiguration apiConfig;

    /**
     * Api response handler configuration
     * */
    private List<List<MethodConfiguration>> responseHandlers;

    /**
     * Check if integration step has pre handler api call operation
     * */
    public boolean hasPreApiCallHandler() {
        return preApiCallHandlers != null && !preApiCallHandlers.isEmpty();
    }

    /**
     * Check if integration step has handlers
     * */
    public boolean hasResponseHandlers() {
        return responseHandlers != null && !responseHandlers.isEmpty();
    }

    @Override
    public String getName(ValueSource source) {
        return null;
    }
}
