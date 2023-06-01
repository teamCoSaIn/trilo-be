package com.cosain.trilo.unit.trip.infra.repository;

import com.cosain.trilo.support.RepositoryTest;
import com.cosain.trilo.trip.domain.entity.Day;
import com.cosain.trilo.trip.domain.entity.Schedule;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.vo.*;
import com.cosain.trilo.trip.infra.repository.day.DayScheduleQueryRepository;
import com.cosain.trilo.trip.infra.dto.DayScheduleDetail;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
@DisplayName("DayQueryRepositoryTest 테스트")
public class DayScheduleQueryRepositoryTest {

    @Autowired
    private DayScheduleQueryRepository dayQueryRepository;

    @Autowired
    private EntityManager em;

    @Test
    void Day_조회를_하면_해당_Day정보와_해당_Day에_속한_Schedule들의_요약정보와_함께_조회된다(){
        // given
        Trip trip = Trip.create(TripTitle.of("여행제목"), 1L);
        em.persist(trip);

        Day day1 = Day.of(LocalDate.of(2023, 5, 10), trip);
        Day day2 = Day.of(LocalDate.of(2023, 5, 20), trip);
        em.persist(day1);
        em.persist(day2);

        Schedule schedule1 = createSchedule(trip, day1,10000L);
        Schedule schedule2 = createSchedule(trip, day1,20000L);
        Schedule schedule3 = createSchedule(trip, day1,30000L);
        Schedule schedule4 = createSchedule(trip, day2,10000L);

        em.persist(schedule1);
        em.persist(schedule2);
        em.persist(schedule3);
        em.persist(schedule4);
        em.flush();

        // when
        long findDayId = day1.getId();
        DayScheduleDetail dayScheduleDetail = dayQueryRepository.findDayWithSchedulesByDayId(findDayId).get();

        // then
        int findSchedulesSize = 3;
        assertThat(dayScheduleDetail.getDayId()).isEqualTo(day1.getId());
        assertThat(dayScheduleDetail.getDate()).isEqualTo(day1.getTripDate());
        assertThat(dayScheduleDetail.getTripId()).isEqualTo(trip.getId());
        assertThat(dayScheduleDetail.getSchedules().size()).isEqualTo(findSchedulesSize);
        assertThat(dayScheduleDetail.getSchedules().get(0).getScheduleId()).isEqualTo(schedule1.getId());
        assertThat(dayScheduleDetail.getSchedules().get(2).getScheduleId()).isEqualTo(schedule3.getId());
    }

    private Schedule createSchedule(Trip trip, Day day, Long scheduleIndexValue){
        return Schedule.builder()
                .trip(trip)
                .day(day)
                .scheduleTitle(ScheduleTitle.of("Test Schedule"))
                .place(Place.of("장소 1", "광산구", Coordinate.of(62.62, 62.62)))
                .scheduleIndex(ScheduleIndex.of(scheduleIndexValue))
                .scheduleContent(ScheduleContent.of("일정 본문"))
                .build();
    }
}
