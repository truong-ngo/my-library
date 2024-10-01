package com.nxt.lib.integration.method;

import com.nxt.lib.integration.IntegrationException;
import com.nxt.lib.integration.ValueSource;
import com.nxt.lib.integration.utils.IntegrationUtils;
import com.nxt.lib.integration.utils.ServiceUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Method executor in integration process
 * @author Truong Ngo
 * */
@Slf4j
@AllArgsConstructor
public class MethodExecutor {

    /**
     * Method configuration
     * */
    private final MethodConfiguration config;

    /**
     * Execute method in integration process
     * @param source: source value
     * @return result of method invocation
     */
    public MethodInvocationResult invokeMethod(ValueSource source) {

        Object returnObject = null; MethodInvocationException exception = null;

        try {
            Class<?> serviceType = getServiceType(config);
            Object serviceInstance = ServiceUtils.getServiceAsObject(serviceType);
            Method method = getMethod(serviceType, config);
            if (method.getParameterCount() > 0) {
                Object[] parameters = extractMethodParameters(method, config.getParametersConfig(), source);
                returnObject = method.invoke(serviceInstance, parameters);
            } else {
                returnObject = method.invoke(serviceInstance);
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            log.info("Method {} has exception during the invocation: {}", config.getMethodPath(), e.getMessage());
            exception = new MethodInvocationException(e, config.getMethodPath());
        }

        MethodInvocationResult result = new MethodInvocationResult(config.getMethodSignature(), returnObject, exception);
        source.cacheMethodResult(config.getMethodSignature(), result);

        return result;
    }

    /**
     * @return service's {@link Class} type
     * */
    private Class<?> getServiceType(MethodConfiguration config) {
        try {
            return Class.forName(config.getServiceType());
        } catch (ClassNotFoundException exception) {
            throw new IntegrationException("Service type not found: " + config.getServiceType());
        }
    }

    /**
     * @return parameter's {@link Class} type
     * */
    private Class<?>[] getParametersType(List<ParameterConfiguration> configurations) {
        try {
            Class<?>[] classes = new Class[configurations.size()];
            for (ParameterConfiguration config : configurations) {
                classes[config.getIndex()] = Class.forName(config.getParametersType());
            }
            return classes;
        } catch (ClassNotFoundException exception) {
            throw new IntegrationException("Parameter type not found: " + exception.getMessage());
        }
    }

    /**
     * @return {@link Method} that need invoked
     */
    private Method getMethod(Class<?> serviceType, MethodConfiguration config) {
        try {
            return serviceType.getMethod(config.getMethodName(), getParametersType(config.getParametersConfig()));
        } catch (NoSuchMethodException exception) {
            throw new IntegrationException("Method " + config.getMethodName() + " not found in service: " + config.getServiceType());
        }
    }

    /**
     * Get parameter value from {@link MethodConfiguration} and {@link ValueSource}
     * @return Array of parameters
     * */
    private Object[] extractMethodParameters(Method method, List<ParameterConfiguration> configs, ValueSource source) {
        Object[] parameters = new Object[method.getParameterCount()];
        for (ParameterConfiguration cfg : configs) {
            Object param = IntegrationUtils.getValueFromConfig(cfg.getParamsConfig(), source);
            parameters[cfg.getIndex()] = param;
        }
        return parameters;
    }
}
