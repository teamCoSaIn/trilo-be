
package com.cosain.trilo.unit.trip.infra.repository;

import com.cosain.trilo.support.RepositoryTest;
import com.cosain.trilo.trip.domain.entity.Schedule;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.vo.*;
import com.cosain.trilo.trip.infra.dto.ScheduleDetail;
import com.cosain.trilo.trip.infra.dto.ScheduleSummary;
import com.cosain.trilo.trip.infra.repository.schedule.ScheduleQueryRepository;
import com.cosain.trilo.trip.presentation.trip.query.dto.request.TempSchedulePageCondition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;


@RepositoryTest
@DisplayName("ScheduleQueryRepository 테스트")
public class ScheduleQueryRepositoryTest {

    @Autowired
    private ScheduleQueryRepository scheduleQueryRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    void findScheduleTest(){
        // given
        Trip trip = Trip.create(TripTitle.of("제목"), 1L);
        em.persist(trip);
        Schedule schedule = Schedule.builder()
                .trip(trip)
                .scheduleTitle(ScheduleTitle.of("Test Schedule"))
                .place(Place.of("장소 1", "광산구", Coordinate.of(62.62, 62.62)))
                .scheduleIndex(ScheduleIndex.ZERO_INDEX)
                .scheduleContent(ScheduleContent.of("일정 본문"))
                .build();
        em.persist(schedule);
        em.flush();

        // when
        ScheduleDetail dto = scheduleQueryRepository.findScheduleDetailById(schedule.getId()).get();

        // then
        assertThat(dto.getScheduleId()).isEqualTo(schedule.getId());
        assertThat(dto.getScheduleId()).isEqualTo(schedule.getId());
        assertThat(dto.getTitle()).isEqualTo(schedule.getScheduleTitle().getValue());
        assertThat(dto.getPlaceName()).isEqualTo(schedule.getPlace().getPlaceName());
        assertThat(dto.getCoordinate().getLatitude()).isEqualTo(schedule.getPlace().getCoordinate().getLatitude());
        assertThat(dto.getCoordinate().getLatitude()).isEqualTo(schedule.getPlace().getCoordinate().getLongitude());
        assertThat(dto.getOrder()).isEqualTo(schedule.getScheduleIndex().getValue());
        assertThat(dto.getContent()).isEqualTo(schedule.getScheduleContent().getValue());
    }

    @Nested
    @DisplayName("임시 보관함 조회 시")
    class findTemporaryScheduleListByTripIdTest{

        @Test
        @DirtiesContext
        @DisplayName("tripId 가 일치하고 dayId 가 null 인 커서 보다 큰 일정들이 size 만큼 조회된다.")
        void findTest(){
            // given
            Trip trip = Trip.create(TripTitle.of("제목"), 1L);
            em.persist(trip);
            Schedule schedule1 = createSchedule(trip, 10000L);
            Schedule schedule2 = createSchedule(trip, 20000L);
            Schedule schedule3 = createSchedule(trip, 30000L);
            Schedule schedule4 = createSchedule(trip, 40000L);
            em.persist(schedule1);
            em.persist(schedule2);
            em.persist(schedule3);
            em.persist(schedule4);
            em.flush();

            TempSchedulePageCondition tempSchedulePageCondition = new TempSchedulePageCondition(schedule1.getId());

            // when
            Slice<ScheduleSummary> scheduleSummaries = scheduleQueryRepository.findTemporaryScheduleListByTripId(1L,tempSchedulePageCondition,PageRequest.ofSize(3));

            // then
            assertThat(scheduleSummaries.getSize()).isEqualTo(3);
        }

        @Test
        @DisplayName("scheduleIndex 기준 오름차순으로 조회된다.")
        void sortTest(){
            // given
            Trip trip = Trip.create(TripTitle.of("제목"), 1L);
            em.persist(trip);
            Schedule schedule1 = createSchedule(trip, 10000L);
            Schedule schedule2 = createSchedule(trip, 20000L);
            Schedule schedule3 = createSchedule(trip, 30000L);
            em.persist(schedule1);
            em.persist(schedule2);
            em.persist(schedule3);
            em.flush();

            TempSchedulePageCondition tempSchedulePageCondition = new TempSchedulePageCondition(schedule1.getId());

            // when
            Slice<ScheduleSummary> scheduleSummaries = scheduleQueryRepository.findTemporaryScheduleListByTripId(1L, tempSchedulePageCondition, PageRequest.ofSize(2));

            // then
            assertThat(scheduleSummaries.getSize()).isEqualTo(2);
            assertThat(scheduleSummaries.getContent().get(0).getScheduleId()).isEqualTo(schedule2.getId());
            assertThat(scheduleSummaries.getContent().get(1).getScheduleId()).isEqualTo(schedule3.getId());
        }

    }

    @Test
    @DirtiesContext
    void existByIdTest(){
        // given
        Trip trip = Trip.create(TripTitle.of("제목"), 1L);
        em.persist(trip);
        Schedule schedule = createSchedule(trip, 10000L);
        em.persist(schedule);
        em.flush();

        // when & then
        long notExistScheduleId = 2L;
        assertThat(scheduleQueryRepository.existById(schedule.getId())).isTrue();
        assertThat(scheduleQueryRepository.existById(notExistScheduleId)).isFalse();
    }

    private Schedule createSchedule(Trip trip, long scheduleIndexValue){
        return Schedule.builder()
                .trip(trip)
                .scheduleTitle(ScheduleTitle.of("Test Schedule"))
                .place(Place.of("장소 1", "광산구", Coordinate.of(62.62, 62.62)))
                .scheduleIndex(ScheduleIndex.of(scheduleIndexValue))
                .scheduleContent(ScheduleContent.of("일정 본문"))
                .build();
    }
}
