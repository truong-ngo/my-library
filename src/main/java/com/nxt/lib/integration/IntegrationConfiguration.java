package com.nxt.lib.integration;

import com.nxt.lib.integration.api.ApiConfiguration;
import com.nxt.lib.integration.method.MethodConfiguration;
import com.nxt.lib.integration.utils.IntegrationUtils;
import lombok.Data;

import java.util.List;

/**
 * Define a complete integration step that include:
 * <ul>
 *   <li>Pre api call handler</li>
 *   <li>Api calling</li>
 *   <li>Response handlers</li>
 *   <li>Next integration steps</li>
 * </ul>
 * @see ApiConfiguration
 * @see MethodConfiguration
 * @author Truong Ngo
 * */
@Data
public class IntegrationConfiguration {

    /**
     * Execute condition of an integration step
     * */
    private String executeCondition;

    /**
     * Pre api call handler's configuration
     * */
    private MethodConfiguration preApiCallHandler;

    /**
     * Api configuration
     * */
    private ApiConfiguration apiConfig;

    /**
     * Api response handler configuration
     * */
    private List<MethodConfiguration> responseHandlers;

    /**
     * Next steps in integration process
     * */
    private List<IntegrationConfiguration> nextSteps;

    /**
     * Check if integration step has pre handler api call operation
     * */
    public boolean hasPreApiCallHandler() {
        return preApiCallHandler != null;
    }

    /**
     * Check if integration step has handlers
     * */
    public boolean hasResponseHandlers() {
        return responseHandlers != null && responseHandlers.size() > 0;
    }

    /**
     * Check if integration step has next steps
     * */
    public boolean hasNextSteps() {
        return nextSteps != null && nextSteps.size() > 0;
    }
}
