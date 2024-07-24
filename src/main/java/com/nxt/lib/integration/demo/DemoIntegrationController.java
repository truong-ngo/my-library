package com.nxt.lib.integration.demo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nxt.lib.integration.*;
import com.nxt.lib.integration.utils.IntegrationUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DemoIntegrationController {

    @GetMapping("/integration")
    public ResponseEntity<?> runIntegration(
            @RequestHeader(name = "basic") String basic,
            @RequestParam("flow") String flow,
            @RequestParam("departmentCode") String code) {
        Map<String, String> input = Map.of("basic", basic, "departmentCode", code);
        ValueSource source = new ValueSource(input);
        List<List<ProcessConfiguration>> configuration = IntegrationUtils.getConfiguration(flow, new TypeReference<>() {});
        IntegrationExecutor executor = new IntegrationExecutor(configuration);
        IntegrationResult result = executor.runIntegration(source);
        return ResponseEntity.ok(Map.of("operationName", result.getLatestOperationName(), "Detail", result.getOperationStatus()));
    }


}
