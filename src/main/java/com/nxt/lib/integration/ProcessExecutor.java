package com.nxt.lib.integration;

import com.nxt.lib.integration.api.*;
import com.nxt.lib.integration.method.*;
import com.nxt.lib.integration.utils.IntegrationUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Execute process declare in an integration step
 * @see ProcessConfiguration
 * @author Truong Ngo
 * */
@Slf4j
public class ProcessExecutor {

    /**
     * Process configuration
     * */
    private final ProcessConfiguration configuration;

    /**
     * Construct the executor with configuration
     * @param configuration: Process configuration
     * */
    public ProcessExecutor(ProcessConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * Execute process's flow
     * <p>
     *     Execute the process and return the last operation in process<br/>
     *     The last operation in process is indicate as rule below: <br/>
     *     <ul>
     *         <li>If only pre call can be invocable and api and handler process cannot be invoke, return the result of pre call</li>
     *         <li>Else if the api is invocable and handler cannot be invoke, return the api call result</li>
     *         <li>Else return the handler result</li>
     *     </ul>
     * </p>
     * @param source: source value for extraction
     * @return the last operation of process
     * */
    public IntegrationResult execute(ValueSource source) {

        /*
         * Invoke pre handler api process
         * If the condition is null or not match then the process is ignored
         */
        MethodInvocationResult preCallResult = invokeHandlers(source, configuration.getPreApiCallHandlers());

        /*
         * Call api process
         * If the condition is null or not match then the process is ignored
         */
        ApiResponse response = null;
        boolean isApiInvokable = isApiInvokable(source);
        if (isApiInvokable) {
            ApiExecutor apiExecutor = new ApiExecutor(configuration.getApiConfig());
            response = apiExecutor.performApiCall(source);
        }

        /*
         * Invoke api response handlers process
         * Only one handler can be invoked in this step
         * If the condition is null or not match then the process is ignored
         */
        MethodInvocationResult handlerResult = invokeHandlers(source, configuration.getResponseHandlers());

        /*
        * Find the last operation in process
        * */
        if (preCallResult != null && !isApiInvokable && handlerResult == null) {
            return new IntegrationResult(preCallResult.methodSignature(), preCallResult);
        } else if (isApiInvokable && handlerResult == null) {
            return new IntegrationResult(configuration.getApiConfig().getName(source), response);
        } else if (handlerResult != null) {
            return new IntegrationResult(handlerResult.methodSignature(), handlerResult);
        } else {
            log.info("Non of element of process can be invoke");
            return null;
        }
    }

    /**
     * @return true if the integration step need to call api
     * */
    private boolean isApiInvokable(ValueSource source) {
        return IntegrationUtils.getCondition(configuration.getApiConfig().getInvokeCondition(), source);
    }

    /**
     * Invoke handlers (pre call, api handle) element
     * @param source extractor source
     * @return result of one of handlers operation, if no handler invoke return null
     * */
    private MethodInvocationResult invokeHandlers(ValueSource source, List<List<MethodConfiguration>> handlersConfig) {
        if (handlersConfig == null || handlersConfig.isEmpty()) {
            return null;
        }
        for (List<MethodConfiguration> handlers : handlersConfig) {
            if (handlers != null && !handlers.isEmpty()) {
                for (MethodConfiguration handler : handlers) {
                    if (handler != null && IntegrationUtils.getCondition(handler.getInvokeCondition(), source)) {
                        MethodExecutor executor = new MethodExecutor(handler);
                        return executor.invokeMethod(source);
                    }
                }
            }
        }
        return null;
    }
}
