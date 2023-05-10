package com.cosain.trilo.unit.trip.query.infra.repository;

import com.cosain.trilo.support.RepositoryTest;
import com.cosain.trilo.trip.command.domain.entity.Schedule;
import com.cosain.trilo.trip.command.domain.entity.Trip;
import com.cosain.trilo.trip.command.domain.vo.Coordinate;
import com.cosain.trilo.trip.command.domain.vo.Place;
import com.cosain.trilo.trip.command.domain.vo.ScheduleIndex;
import com.cosain.trilo.trip.query.domain.dto.ScheduleDto;
import com.cosain.trilo.trip.query.domain.repository.ScheduleQueryRepository;
import com.cosain.trilo.trip.query.infra.dto.ScheduleDetail;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

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
}
