package com.nxt.lib.integration.method;

import lombok.Data;

import java.util.List;

/**
 * Configuration for method that invoke in integration process
 * */
@Data
public class MethodConfiguration {

    /**
     * Indicate service's type that have invoked method.<br/>
     * */
    private String serviceType;

    /**
     * Indicate method name
     * */
    private String methodName;

    /**
     * Indicate list of method's parameter type
     * */
    private List<String> parametersType;

    /**
     * Configuration for parameter
     * */
    private List<ParameterConfiguration> paramsConfig;

    /**
     * Indicate invoke condition
     * */
    private String invokeCondition;

    /**
     * Exception handler
     * */
    private MethodConfiguration exceptionHandler;

    /**
     * Indicate next method that has to be executed
     * */
    private List<MethodConfiguration> nextMethods;

}
