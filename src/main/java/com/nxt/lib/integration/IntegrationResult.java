package com.nxt.lib.integration;

import com.nxt.lib.integration.api.ApiResponse;
import com.nxt.lib.integration.method.MethodInvocationResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Result of integration process
 * <p>The result is the result of final operation in process that can be track
 * if error occur</p>
 * @see IntegrationExecutor
 * @author Truong Ngo
 * */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntegrationResult {

    /**
     * Indicate final operation of integration process (method signature, api name)
     * */
    private String latestOperationName;

    /**
     * Indicate the value of the latest operation
     * */
    private Object latestOperationResult;

    /**
     * Get operation status
     * */
    public String getOperationStatus() {
        if (latestOperationResult instanceof ApiResponse res) {
            return res.response() != null ?
                    "Status: " + res.response().getStatusCode().value() :
                    "Exception: " + res.exception().getCause().getClass().getSimpleName();
        }
        if (latestOperationResult instanceof MethodInvocationResult result) {
            return result.result() != null ? "Status: OK" : "Exception: " + result.exception().getCause().getClass().getSimpleName();
        }
        return "";
    }
}

