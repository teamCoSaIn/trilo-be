package com.cosain.trilo.deploy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeployController {

    @Value("${deploy-module.version}")
    private String version;

    @GetMapping("/deploy/version")
    public String version() {
        return String.format("Project.version : %s", version);
    }

    @GetMapping("/deploy/health")
    public String checkHealth() {
        return "healthy";
    }
}
