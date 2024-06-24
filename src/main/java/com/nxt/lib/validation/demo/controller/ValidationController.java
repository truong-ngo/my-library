package com.nxt.lib.validation.demo.controller;

import com.nxt.lib.validation.core.Validated;
import com.nxt.lib.validation.demo.model.Organization;
import com.nxt.lib.validation.core.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api")
public class ValidationController {

    @PostMapping
    @Validated
    public ResponseEntity<Organization> createOrganization(@RequestBody @Valid(rule = "validation/organization.json") Organization organization) {
        log.info("org: {}", organization);
        return ResponseEntity.ok(organization);
    }
}
