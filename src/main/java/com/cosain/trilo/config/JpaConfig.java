package com.cosain.trilo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
        value = {"com.cosain.trilo.trip.domain.repository",
                "com.cosain.trilo.user.domain",
                "com.cosain.trilo.trip.infra.repository.trip.jpa",
                "com.cosain.trilo.trip.infra.repository.schedule.jpa",
                "com.cosain.trilo.trip.infra.repository.day.jpa"
        }
)
public class JpaConfig {
}
