package com.nxt.lib.integration;

import com.nxt.lib.integration.utils.IntegrationUtils;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Execute integration pipeline
 * @author Truong Ngo
 * */
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class IntegrationExecutor {

    /**
     * Configuration
     * */
    private List<List<ProcessConfiguration>> configuration;

    /**
     * Run integration pipeline
     * @param source source value for extraction
     * @return integration result
     * */
    public IntegrationResult runIntegration(ValueSource source) {
        List<IntegrationResult> result = new ArrayList<>();
        for (List<ProcessConfiguration> processes : configuration) {
            for (ProcessConfiguration process : processes) {
                if (IntegrationUtils.getCondition(process.getInvokeCondition(), source)) {
                    ProcessExecutor executor = new ProcessExecutor(process);
                    IntegrationResult processResult = executor.execute(source);
                    result.add(processResult);
                }
            }
        }
        if (!result.isEmpty()) {
            return result.get(result.size() - 1);
        }
        log.info("Invalid integration configuration!");
        throw new RuntimeException("Invalid integration configuration!");
    }
}
