package com.cosain.trilo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
        value = {"com.cosain.trilo.trip.command.domain",
                "com.cosain.trilo.user.domain",
                "com.cosain.trilo.trip.query.infra.repository.trip.jpa",
                "com.cosain.trilo.trip.query.infra.repository.schedule.jpa",
                "com.cosain.trilo.trip.query.infra.repository.day.jpa"
        }
)
public class JpaConfig {
}
