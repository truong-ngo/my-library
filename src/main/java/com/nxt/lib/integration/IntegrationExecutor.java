package com.nxt.lib.integration;

import com.nxt.lib.integration.api.*;
import com.nxt.lib.integration.method.MethodConfiguration;
import com.nxt.lib.integration.method.MethodExecutor;
import com.nxt.lib.integration.method.MethodInvocationResult;
import com.nxt.lib.integration.utils.IntegrationUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * Execute integration process that define in {@link IntegrationConfiguration}
 * @author Truong Ngo
 * */
@Slf4j
public class IntegrationExecutor {

    /**
     * Integration step configuration
     * */
    private final IntegrationConfiguration configuration;

    /**
     * Construct the executor with configuration
     * @param configuration: Integration configuration
     * */
    public IntegrationExecutor(IntegrationConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * Execute integration's flow
     * <p>
     *     Execute the integration flow and return the last operation<br/>
     *     If the next step is cannot be invoked then return the last operation in step<br/>
     *     The last step is indicate as rule below: <br/>
     *     <ul>
     *         <li>If process has invokable next step, return the invoke next step</li>
     *         <li>If only pre call can be invokable and api and handler process cannot be invoke, return the result of pre call</li>
     *         <li>Else if the api is invokable and handler cannot be invoke, return the api call result</li>
     *         <li>Else return the handler result</li>
     *     </ul>
     * </p>
     * @param source: source value for extraction
     * @return the last operation of integration process
     * */
    public IntegrationResult execute(ValueSource source) {

        /*
         * Invoke pre handler api process
         * If the condition is null or not match then the process is ignored
         */
        MethodInvocationResult preCallResult = null;
        boolean isPreCallHandlerInvokable = isPreCallHandlerInvokable(source);
        if (isPreCallHandlerInvokable) {
            MethodExecutor preCallExecutor = new MethodExecutor(configuration.getPreApiCallHandler());
            preCallResult = preCallExecutor.invokeMethod(source);
        }

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
        MethodInvocationResult handlerResult = null;
        boolean isResponseHandlerInvokable = isResponseHandlerInvokable(source);
        if (isResponseHandlerInvokable) {
            for (MethodConfiguration handler : configuration.getResponseHandlers()) {
                if (IntegrationUtils.getCondition(handler.getInvokeCondition(), source)) {
                    MethodExecutor handlerExecutor = new MethodExecutor(handler);
                    handlerResult = handlerExecutor.invokeMethod(source);
                }
            }
        }

        /*
         * Invoke next steps
         * Only one in these next steps can be invoked
         * If the condition is null or not match then the process is ignored
         * If next steps is invokable the return the result of the invoked step
         */
        boolean isNextStepInvokable = isNextStepInvokable(source);
        if (isNextStepInvokable) {
            for (IntegrationConfiguration nextStep : configuration.getNextSteps()) {
                if (IntegrationUtils.getCondition(nextStep.getExecuteCondition(), source)) {
                    IntegrationExecutor nextStepExecutor = new IntegrationExecutor(nextStep);
                    return nextStepExecutor.execute(source);
                }
            }
        }

        /*
        * Find the last operation in step
        * If none of next steps is invoked, return the last operation of step
        * */
        if (isPreCallHandlerInvokable && !isApiInvokable && !isResponseHandlerInvokable) {
            return new IntegrationResult(configuration.getPreApiCallHandler().getMethodSignature(), preCallResult);
        } else if (isApiInvokable && !isResponseHandlerInvokable) {
            return new IntegrationResult(configuration.getApiConfig().getApiName(), response);
        } else if (isResponseHandlerInvokable) {
            String handlerSignature = configuration
                    .getResponseHandlers()
                    .stream()
                    .filter(handler -> IntegrationUtils.getCondition(handler.getInvokeCondition(), source))
                    .map(MethodConfiguration::getMethodSignature)
                    .findFirst()
                    .orElse("");

            return new IntegrationResult(handlerSignature, handlerResult);
        } else {
            log.info("Non of process in these step can be invoke");
            return null;
        }
    }

    /**
     * @return true if the integration step need the pre call handler
     * */
    private boolean isPreCallHandlerInvokable(ValueSource source) {
        return configuration.hasPreApiCallHandler() &&
               IntegrationUtils.getCondition(configuration.getPreApiCallHandler().getInvokeCondition(), source);
    }

    /**
     * @return true if the integration step need to call api
     * */
    private boolean isApiInvokable(ValueSource source) {
        return IntegrationUtils.getCondition(configuration.getApiConfig().getCallCondition(), source);
    }

    /**
     * @return true if the integration step need to handler the api response
     * */
    private boolean isResponseHandlerInvokable(ValueSource source) {
        if (!configuration.hasResponseHandlers()) {
            return false;
        }
        return configuration.getResponseHandlers()
                .stream()
                .anyMatch(handler -> IntegrationUtils.getCondition(handler.getInvokeCondition(), source));
    }

    /**
     * @return true if the integration step need to invoke next step
     * */
    private boolean isNextStepInvokable(ValueSource source) {
        return configuration.hasNextSteps() &&
               configuration.getNextSteps()
                       .stream()
                       .anyMatch(nextStep -> IntegrationUtils.getCondition(nextStep.getExecuteCondition(), source));
    }
}
