package com.cosain.trilo.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;

@TestConfiguration
@ComponentScan(basePackages = "com.cosain.trilo.config.security")
public class SecurityTestConfig {
}
