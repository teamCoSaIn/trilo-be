package com.cosain.trilo.unit.trip.command.domain.repository;

import com.cosain.trilo.support.RepositoryTest;
import com.cosain.trilo.trip.command.domain.entity.Day;
import com.cosain.trilo.trip.command.domain.entity.Trip;
import com.cosain.trilo.trip.command.domain.repository.DayRepository;
import com.cosain.trilo.trip.command.domain.vo.TripPeriod;
import com.cosain.trilo.trip.command.domain.vo.TripStatus;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
public class DayRepositoryTest {

    @Autowired
    private DayRepository dayRepository;

    @Autowired
    private EntityManager em;

    @Test
    void deleteDaysTest() {

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

    @Test
    @DirtiesContext
    @DisplayName("deleteAllByTripId로 Day를 삭제하면, 해당 여행의 모든 Day들이 삭제된다.")
    void deleteAllByTripIdTest() {
        // given
        Trip trip = Trip.builder()
                .tripperId(1L)
                .title("여행 제목")
                .status(TripStatus.DECIDED)
                .tripPeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 3)))
                .build();

        em.persist(trip);

        Day day1 = Day.of(LocalDate.of(2023, 3, 1), trip);
        Day day2 = Day.of(LocalDate.of(2023, 3, 2), trip);
        Day day3 = Day.of(LocalDate.of(2023, 3, 3), trip);

        em.persist(day1);
        em.persist(day2);
        em.persist(day3);

        // when
        dayRepository.deleteAllByTripId(trip.getId());
        em.clear();

        // then
        List<Day> findDays = dayRepository.findAllById(List.of(day1.getId(), day2.getId(), day3.getId()));
        assertThat(findDays).isEmpty();
    }
}
