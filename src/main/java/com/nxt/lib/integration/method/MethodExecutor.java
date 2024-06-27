package com.nxt.lib.integration.method;

import com.nxt.lib.integration.IntegrationException;
import com.nxt.lib.integration.ValueSource;
import com.nxt.lib.integration.utils.IntegrationUtils;
import com.nxt.lib.integration.utils.ServiceUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Method executor in integration process
 * */
@Slf4j
public class MethodExecutor {

    /**
     * Method configuration
     * */
    private final MethodConfiguration config;

    public MethodExecutor(MethodConfiguration config) {
        this.config = config;
    }

    /**
     * Execute method in integration process
     * @param source: source value
     * @return result of method invocation
     */
    public MethodInvocationResult invokeMethod(ValueSource source) {

        Object returnObject = null;
        Exception exception = null;

        try {
            Class<?> serviceType = getServiceType(config);
            Object serviceInstance = ServiceUtils.getServiceAsObject(serviceType);
            Method method = getMethod(serviceType, config);
            if (method.getParameterCount() > 0) {
                Object[] parameters = extractMethodParameter(method, config.getParamsConfig(), source);
                returnObject = method.invoke(serviceInstance, parameters);
            } else {
                returnObject = method.invoke(serviceInstance);
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            log.info("Exception occur: {}", e.getMessage());
            exception = e;
        }

        MethodInvocationResult result = new MethodInvocationResult(returnObject, exception);
        source.cacheMethodResult(config.getMethodSignature(), result);

        if (config.getExceptionHandler() != null) {
            MethodExecutor handler = new MethodExecutor(config.getExceptionHandler());
            handler.invokeMethod(source);
        }

        List<MethodConfiguration> nextMethods = config.getNextMethods();
        if (nextMethods == null || nextMethods.size() == 0) {
            return result;
        }

        for (MethodConfiguration nextMethod : nextMethods) {
            Boolean condition = IntegrationUtils.extractCondition(nextMethod.getInvokeCondition(), source);
            if (condition) {
                MethodExecutor nextMethodExecutor = new MethodExecutor(nextMethod);
                return nextMethodExecutor.invokeMethod(source);
            }
        }
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
    private Class<?>[] getParametersType(List<String> parameterTypes) {
        try {
            Class<?>[] classes = new Class[parameterTypes.size()];
            for (int i = 0; i < parameterTypes.size(); i++) {
                classes[i] = Class.forName(parameterTypes.get(i));
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
            return serviceType.getMethod(config.getMethodName(), getParametersType(config.getParametersType()));
        } catch (NoSuchMethodException exception) {
            throw new IntegrationException("Method " + config.getMethodName() + " not found in service: " + config.getServiceType());
        }
    }

    /**
     * Get parameter value from {@link MethodConfiguration} and {@link ValueSource}
     * @return Array of parameters
     * */
    private Object[] extractMethodParameter(Method method, List<ParameterConfiguration> configs, ValueSource source) {
        Object[] parameters = new Object[method.getParameterCount()];
        Object param;
        for (ParameterConfiguration cfg : configs) {
            if (cfg.getSimpleExtractExpression() != null) {
                param = IntegrationUtils.extractValue(cfg.getSimpleExtractExpression(), source);
            } else {
                param = cfg.getCompositeExtractExpression()
                        .entrySet()
                        .stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> IntegrationUtils.extractValue(entry.getKey(), source)
                        ));
            }
            parameters[cfg.getParamIndex()] = param;
        }
        return parameters;
    }
}
