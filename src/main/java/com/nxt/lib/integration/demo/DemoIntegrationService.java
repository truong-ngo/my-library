package com.nxt.lib.integration.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.util.Map;

@Slf4j
@Component
public class DemoIntegrationService {

    public Map<String, String> getUserFromHeader(String basic) {
        String[] data = basic.split(":");
        return Map.of("name", data[0], "pw", data[1]);
    }

    public void saveExceptionLog(RestClientException e) {
        log.info("persist exception with message: {} to database", e.getMessage());
        System.out.println(e.getMessage());
    }
}
