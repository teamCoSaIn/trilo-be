package com.cosain.trilo.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@TestConfiguration
public class ClockConfig {
    @Bean
    public Clock fixedClock() {
        LocalDate fixedDate = LocalDate.of(2023, 4, 28);
        Instant fixedInstant = fixedDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        return Clock.fixed(fixedInstant, ZoneId.systemDefault());
    }

}
