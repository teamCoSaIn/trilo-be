package com.cosain.trilo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableRedisRepositories(basePackages = "com.cosain.trilo.auth.infra.repository")
public class RedisConfig {
}
