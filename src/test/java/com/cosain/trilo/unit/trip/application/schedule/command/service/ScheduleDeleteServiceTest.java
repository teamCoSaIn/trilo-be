package com.cosain.trilo.unit.trip.application.schedule.command.service;

import com.cosain.trilo.fixture.ScheduleFixture;
import com.cosain.trilo.fixture.TripFixture;
import com.cosain.trilo.trip.application.exception.NoScheduleDeleteAuthorityException;
import com.cosain.trilo.trip.application.exception.ScheduleNotFoundException;
import com.cosain.trilo.trip.application.schedule.command.service.ScheduleDeleteService;
import com.cosain.trilo.trip.domain.entity.Day;
import com.cosain.trilo.trip.domain.entity.Schedule;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.repository.ScheduleRepository;
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
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
    @DisplayName("정상적인 일정 삭제 요청 -> 리포지토리 호출 횟수 검증")
    public void deleteSuccessTest() {
        // given
        Long tripId = 1L;
        Long tripOwnerId = 2L;
        Long deleteTripperId = 2L;
        Long scheduleId = 3L;
        LocalDate startDate = LocalDate.of(2023,3,1);
        LocalDate endDate = LocalDate.of(2023,3,1);

        // mock: 리포지토리에서 가져올 Schedule 설정
        Trip trip = TripFixture.decided_Id(tripId, tripOwnerId, startDate, endDate, 1L);
        Day day = trip.getDays().get(0);
        Schedule schedule = ScheduleFixture.day_Id(scheduleId, trip, day, 0L);
        given(scheduleRepository.findByIdWithTrip(eq(scheduleId))).willReturn(Optional.of(schedule));

        // when : 서비스에 일정 삭제 요청
        scheduleDeleteService.deleteSchedule(scheduleId, deleteTripperId);

        // then : 리포지토리 호출 횟수 검증
        verify(scheduleRepository).findByIdWithTrip(eq(scheduleId));
        verify(scheduleRepository).delete(any(Schedule.class));
    }

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
            Long scheduleId = 4L;
            LocalDate startDate = LocalDate.of(2023,3,1);
            LocalDate endDate = LocalDate.of(2023,3,1);

            // mock: 리포지토리에서 가져올 Schedule 설정
            Trip trip = TripFixture.decided_Id(tripId, tripOwnerId, startDate, endDate, 1L);
            Day day = trip.getDays().get(0);
            Schedule schedule = ScheduleFixture.day_Id(scheduleId, trip, day, 0);
            given(scheduleRepository.findByIdWithTrip(eq(scheduleId))).willReturn(Optional.of(schedule));

            // when & then : 소유자가 아닌 사람의 삭제 요청 -> 발생 예외 및 리포지토리 호출 횟수 검증
            Long invalidTripperId = 3L;
            assertThatThrownBy(() -> scheduleDeleteService.deleteSchedule(scheduleId, invalidTripperId))
                    .isInstanceOf(NoScheduleDeleteAuthorityException.class);
            verify(scheduleRepository).findByIdWithTrip(eq(scheduleId));
        }
    }

}
