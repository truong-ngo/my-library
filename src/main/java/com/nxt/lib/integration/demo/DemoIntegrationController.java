package com.nxt.lib.integration.demo;

import com.nxt.lib.integration.IntegrationExecutor;
import com.nxt.lib.integration.api.ApiFlowConfiguration;
import com.nxt.lib.integration.api.ApiResponse;
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
        IntegrationExecutor executor = new IntegrationExecutor(input);
        ApiFlowConfiguration configuration = IntegrationExecutor.getApiFlowConfiguration(flow);
        ApiResponse response = executor.execute(configuration);
        Object result = response.response() != null ? response.response().getBody() : response.exception().getMessage();
        return ResponseEntity.ok(result);
    }


}
