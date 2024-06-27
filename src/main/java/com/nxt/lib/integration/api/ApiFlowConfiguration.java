package com.nxt.lib.integration.api;

import com.nxt.lib.integration.method.MethodConfiguration;
import lombok.Data;

import java.util.List;

/**
 * Define a step of api integration process
 * */
@Data
public class ApiFlowConfiguration {

    /**
     * Before call api configuration
     * */
    private MethodConfiguration preCall;

    /**
     * Api configuration
     * */
    private ApiConfiguration apiConfig;

    /**
     * Call condition
     * */
    private String callCondition;

    /**
     * Api response handler
     * */
    private List<MethodConfiguration> responseHandlers;

    /**
     * Next step in integration process
     * */
    private List<ApiFlowConfiguration> nextStep;
}
