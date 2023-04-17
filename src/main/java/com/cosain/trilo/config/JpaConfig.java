package com.cosain.trilo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
        value = {"com.cosain.trilo.trip.command.domain", "com.cosain.trilo.user.domain"}
)
public class JpaConfig {
}
