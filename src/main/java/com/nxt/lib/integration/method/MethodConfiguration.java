package com.nxt.lib.integration.method;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nxt.lib.integration.OperationalConfiguration;
import com.nxt.lib.integration.ValueSource;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Configuration for method that invoked in integration process
 * @see MethodExecutor
 * @author Truong Ngo
 * */
@Data
public class MethodConfiguration implements OperationalConfiguration {

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
     * Get method fully qualify path
     * <p>
     * Eg: com.nxt.utils.ClassUtils.isPrimitive(java.lang.Object)
     * </p>
     * */
    public String getMethodPath() {
        return String.format("%s.%s(%s)", serviceType, methodName, String.join(", ", parametersType));
    }

    @Override
    @JsonIgnore
    public String getName(ValueSource source) {
        return methodSignature;
    }
}
