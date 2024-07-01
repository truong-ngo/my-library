package com.nxt.lib.integration.demo;

import com.nxt.lib.integration.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class DemoIntegrationService {

    public Map<String, String> getUserFromHeader(String basic) {
        String[] data = basic.split(":");
        return Map.of("name", data[0], "pw", data[1]);
    }

    public void saveExceptionLog(ApiException e) {
        log.info("persist exception with message: {} to database", e.getCause().getMessage());
        System.out.println(e.getCause().getMessage());
    }
}
