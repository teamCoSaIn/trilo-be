package com.cosain.trilo.unit.trip.query.infra.repository;

import com.cosain.trilo.support.RepositoryTest;
import com.cosain.trilo.trip.command.domain.entity.Schedule;
import com.cosain.trilo.trip.command.domain.entity.Trip;
import com.cosain.trilo.trip.command.domain.vo.Coordinate;
import com.cosain.trilo.trip.command.domain.vo.Place;
import com.cosain.trilo.trip.command.domain.vo.ScheduleIndex;
import com.cosain.trilo.trip.query.domain.dto.ScheduleDto;
import com.cosain.trilo.trip.query.domain.repository.ScheduleQueryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

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
        Trip trip = Trip.create("제목", 1L);
        em.persist(trip);
        Schedule schedule = Schedule.builder()
                .trip(trip)
                .title("Test Schedule")
                .place(Place.of("장소 1", "광산구", Coordinate.of(62.62, 62.62)))
                .scheduleIndex(ScheduleIndex.ZERO_INDEX)
                .content("Test Content")
                .build();
        em.persist(schedule);
        em.flush();

        // when
        ScheduleDto scheduleDto = scheduleQueryRepository.findScheduleDetailByScheduleId(schedule.getId()).get();

        // then
        assertThat(scheduleDto.getScheduleId()).isEqualTo(schedule.getId());
        assertThat(scheduleDto.getScheduleId()).isEqualTo(schedule.getId());
        assertThat(scheduleDto.getTitle()).isEqualTo(schedule.getTitle());
        assertThat(scheduleDto.getPlaceName()).isEqualTo(schedule.getPlace().getPlaceName());
        assertThat(scheduleDto.getLatitude()).isEqualTo(schedule.getPlace().getCoordinate().getLatitude());
        assertThat(scheduleDto.getLongitude()).isEqualTo(schedule.getPlace().getCoordinate().getLongitude());
        assertThat(scheduleDto.getOrder()).isEqualTo(schedule.getScheduleIndex().getValue());
        assertThat(scheduleDto.getContent()).isEqualTo(schedule.getContent());

    }

    @Nested
    @DisplayName("임시 보관함 조회 시")
    class findTemporaryScheduleListByTripIdTest{

        @Test
        @DirtiesContext
        @DisplayName("tripId 가 일치하고 dayId 가 null 인 일정들이 페이지의 크기만큼 조회된다.")
        void findTest(){
            // given
            Trip trip = Trip.create("제목", 1L);
            em.persist(trip);
            Schedule schedule = createSchedule(trip, 1L);
            em.persist(schedule);
            em.flush();

            // when
            Slice<ScheduleDto> temporaryScheduleListByTripId = scheduleQueryRepository.findTemporaryScheduleListByTripId(1L, PageRequest.of(0, 1));

            // then
            assertThat(temporaryScheduleListByTripId.getSize()).isEqualTo(1);
        }

        @Test
        @DisplayName("scheduleIndex 기준 오름차순으로 조회된다.")
        void sortTest(){
            // given
            Trip trip = Trip.create("제목", 1L);
            em.persist(trip);
            Schedule schedule1 = createSchedule(trip, 1L);
            Schedule schedule2 = createSchedule(trip, 2L);
            Schedule schedule3 = createSchedule(trip, 3L);
            em.persist(schedule1);
            em.persist(schedule2);
            em.persist(schedule3);
            em.flush();

            // when
            Slice<ScheduleDto> temporaryScheduleListByTripId = scheduleQueryRepository.findTemporaryScheduleListByTripId(1L, PageRequest.of(0, 3));

            // then
            assertThat(temporaryScheduleListByTripId.getSize()).isEqualTo(3);
            List<ScheduleDto> scheduleList = temporaryScheduleListByTripId.getContent();
            assertThat(scheduleList).hasSize(3);
            assertThat(scheduleList.get(0).getOrder()).isEqualTo(1L);
            assertThat(scheduleList.get(1).getOrder()).isEqualTo(2L);
            assertThat(scheduleList.get(2).getOrder()).isEqualTo(3L);
        }
        private Schedule createSchedule(Trip trip, long scheduleIndexValue){
            return Schedule.builder()
                    .trip(trip)
                    .title("Test Schedule")
                    .place(Place.of("장소 1", "광산구", Coordinate.of(62.62, 62.62)))
                    .scheduleIndex(ScheduleIndex.of(scheduleIndexValue))
                    .content("Test Content")
                    .build();
        }
    }
}
