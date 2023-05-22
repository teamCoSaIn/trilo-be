package com.cosain.trilo.unit.trip.application.schedule.command.service;

import com.cosain.trilo.trip.application.exception.NoScheduleDeleteAuthorityException;
import com.cosain.trilo.trip.application.exception.ScheduleNotFoundException;
import com.cosain.trilo.trip.application.schedule.command.service.ScheduleDeleteService;
import com.cosain.trilo.trip.domain.entity.Day;
import com.cosain.trilo.trip.domain.entity.Schedule;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.repository.ScheduleRepository;
import com.cosain.trilo.trip.domain.vo.Coordinate;
import com.cosain.trilo.trip.domain.vo.Place;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static com.cosain.trilo.fixture.TripFixture.DECIDED_TRIP;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;

@Slf4j
@ExtendWith(MockitoExtension.class)
@DisplayName("[TripCommand] ScheduleDeleteService 테스트")
public class ScheduleDeleteServiceTest {

    @InjectMocks
    private ScheduleDeleteService scheduleDeleteService;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Test
    @DisplayName("존재하지 않는 일정을 삭제하려 하면, ScheduleNotFoundException 발생")
    public void if_delete_not_exist_schedule_then_it_throws_ScheduleNotFoundException() {
        // given
        Long scheduleId = 1L;
        Long deleteTripperId = 1L;
        given(scheduleRepository.findByIdWithTrip(eq(scheduleId))).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> scheduleDeleteService.deleteSchedule(scheduleId, deleteTripperId))
                .isInstanceOf(ScheduleNotFoundException.class);
        verify(scheduleRepository).findByIdWithTrip(eq(scheduleId));
    }

    @Nested
    @DisplayName("일정의 소유자가 아닌 사람이 일정을 삭제하려 하면")
    class When_delete_tripper_not_equals_trip_owner {

        @Test
        @DisplayName("NoScheduleDeleteAuthorityException 이 발생한다.")
        void it_throws_NoScheduleDeleteAuthorityException() {
            // given
            Long tripId = 1L;
            Long tripOwnerId = 2L;
            Long invalidTripperId = 3L;
            Long scheduleId = 4L;

            Trip trip = DECIDED_TRIP.createDecided(tripId, tripOwnerId, "여행 제목", LocalDate.of(2023,3,1), LocalDate.of(2023,3,1));
            Day day = Day.of(LocalDate.of(2023,3,1),trip);
            Schedule schedule = Schedule.builder()
                            .id(scheduleId)
                            .title("일정")
                            .day(day)
                            .trip(trip)
                            .place(Place.of("place-id", "광안리 해수욕장", Coordinate.of(35.1551, 129.1220)))
                            .build();

            given(scheduleRepository.findByIdWithTrip(eq(scheduleId))).willReturn(Optional.of(schedule));

            // when & then
            assertThatThrownBy(() -> scheduleDeleteService.deleteSchedule(scheduleId, invalidTripperId))
                    .isInstanceOf(NoScheduleDeleteAuthorityException.class);

            verify(scheduleRepository).findByIdWithTrip(eq(scheduleId));
        }
    }

    @Test
    @DisplayName("정상 삭제 요청이 들어왔을 때, 리포지토리가 정상적으로 호출되는 지 여부 테스트")
    public void deleteSuccess_and_Repository_Called_Test() {
        // given
        Long tripId = 1L;
        Long tripOwnerId = 2L;
        Long deleteTripperId = 2L;
        Long scheduleId = 3L;

        Trip trip = DECIDED_TRIP.createDecided(tripId, tripOwnerId, "여행 제목", LocalDate.of(2023,3,1), LocalDate.of(2023,3,1));
        Day day = Day.of(LocalDate.of(2023,3,1),trip);
        Schedule schedule = Schedule.builder()
                .id(scheduleId)
                .title("일정")
                .day(day)
                .trip(trip)
                .place(Place.of("place-id", "광안리 해수욕장", Coordinate.of(35.1551, 129.1220)))
                .build();

        given(scheduleRepository.findByIdWithTrip(eq(scheduleId))).willReturn(Optional.of(schedule));
        willDoNothing().given(scheduleRepository).delete(any(Schedule.class));


        // when
        scheduleDeleteService.deleteSchedule(scheduleId, deleteTripperId);

        // then
        verify(scheduleRepository).findByIdWithTrip(eq(scheduleId));
        verify(scheduleRepository).delete(any(Schedule.class));
    }

}
