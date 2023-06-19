package com.cosain.trilo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
        value = {"com.cosain.trilo.trip.domain.repository",
                "com.cosain.trilo.user.domain",
                "com.cosain.trilo.trip.infra.repository"
        }
)
public class JpaConfig {
}
