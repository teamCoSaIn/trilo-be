package com.cosain.trilo.unit.trip.command.domain.repository;

import com.cosain.trilo.trip.command.domain.entity.Day;
import com.cosain.trilo.trip.command.domain.entity.Trip;
import com.cosain.trilo.trip.command.domain.repository.DayRepository;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class DayRepositoryTest {

    @Autowired
    private DayRepository dayRepository;

    @Autowired
    private EntityManager em;

    @Test
    void deleteDaysTest(){

        // given
        Trip trip = Trip.create("제목", 1L);
        em.persist(trip);

        Day day1 = Day.of(LocalDate.of(2023, 5, 1), trip);
        Day day2 = Day.of(LocalDate.of(2023, 5, 2), trip);
        Day day3 = Day.of(LocalDate.of(2023, 5, 3), trip);
        List<Day> daysToDelete = Arrays.asList(day1, day2, day3);

        dayRepository.saveAll(daysToDelete);

        // when
        dayRepository.deleteDays(daysToDelete);

        // then
        List<Day> remainingDays = dayRepository.findAll();
        Assertions.assertThat(remainingDays.size()).isEqualTo(0);
    }
}
