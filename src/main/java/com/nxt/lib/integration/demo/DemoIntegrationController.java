package com.nxt.lib.integration.demo;

import com.nxt.lib.integration.IntegrationExecutor;
import com.nxt.lib.integration.IntegrationConfiguration;
import com.nxt.lib.integration.IntegrationResult;
import com.nxt.lib.integration.ValueSource;
import com.nxt.lib.integration.utils.IntegrationUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        IntegrationConfiguration configuration = IntegrationUtils.getConfiguration(flow, IntegrationConfiguration.class);
        IntegrationExecutor executor = new IntegrationExecutor(configuration);
        IntegrationResult result = executor.execute(source);
        return ResponseEntity.ok(Map.of("operationName", result.getLatestOperationName(), "Detail", result.getOperationStatus()));
    }


}
