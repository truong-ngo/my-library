package com.nxt.lib.integration.method;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Configuration for method that invoked in integration process
 * @author Truong Ngo
 * */
@Data
public class MethodConfiguration {

    /**
     * Indicate service's type that have invoked method.<br/>
     * */
    private String serviceType;

    /**
     * Indicate invoke condition's expression
     * */
    private String invokeCondition;

    /**
     * Indicate method signature (unique identifier of method)
     * */
    private String methodSignature;

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
     * <p>
     * Key: index of parameter<br/>
     * Value: value extractor configuration
     * </p>
     * */
    private Map<Integer, Object> paramsConfig;

    /**
     * Indicate next method that has to be executed
     * */
    private List<MethodConfiguration> nextMethods;

    /**
     * Get method fully qualify path
     * <p>
     * Eg: com.nxt.utils.ClassUtils.isPrimitive(java.lang.Object)
     * </p>
     * */
    public String getMethodPath() {
        return String.format("%s.%s(%s)", serviceType, methodName, String.join(", ", parametersType));
    }

    /**
     * Check if method configuration has next methods
     * @return true if nextMethods is not empty
     * */
    public boolean hasNextMethods() {
        return nextMethods != null && !nextMethods.isEmpty();
    }
}
