package com.cosain.trilo.support;

import com.cosain.trilo.config.QueryDslConfig;
import com.cosain.trilo.trip.infra.repository.day.DayQueryRepository;
import com.cosain.trilo.trip.infra.repository.schedule.ScheduleQueryRepositoryImpl;
import com.cosain.trilo.trip.infra.repository.trip.TripQueryRepositoryImpl;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
@DirtiesContext
@Import({QueryDslConfig.class, TripQueryRepositoryImpl.class, ScheduleQueryRepositoryImpl.class, DayQueryRepository.class})
public @interface RepositoryTest {
}
