package com.cosain.trilo.unit.trip.application.trip.service.trip_period_update;

import com.cosain.trilo.fixture.TripFixture;
import com.cosain.trilo.common.exception.trip.NoTripUpdateAuthorityException;
import com.cosain.trilo.common.exception.trip.TripNotFoundException;
import com.cosain.trilo.trip.application.trip.service.trip_period_update.TripPeriodUpdateCommand;
import com.cosain.trilo.trip.application.trip.service.trip_period_update.TripPeriodUpdateService;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.repository.DayRepository;
import com.cosain.trilo.trip.domain.repository.ScheduleRepository;
import com.cosain.trilo.trip.domain.repository.TripRepository;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("[TripCommand] TripPeriodUpdateService 테스트")
public class TripPeriodUpdateServiceTest {

    @InjectMocks
    private TripPeriodUpdateService tripPeriodUpdateService;

    @Mock
    private TripRepository tripRepository;

    @Mock
    private DayRepository dayRepository;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Test
    @DisplayName("존재하지 않는 여행의 기간을 수정하려 하면, TripNotFoundException 발생")
    public void if_update_not_exist_trip_then_it_throws_TripNotFoundException() {
        // given
        Long tripId = 1L;
        Long tripperId = 2L;

        LocalDate startDate = LocalDate.of(2023, 3, 1);
        LocalDate endDate = LocalDate.of(2023, 3, 3);

        TripPeriodUpdateCommand command = TripPeriodUpdateCommand.of(tripId, tripperId, startDate, endDate);
        given(tripRepository.findByIdWithDays(eq(tripId))).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> tripPeriodUpdateService.updateTripPeriod(command))
                .isInstanceOf(TripNotFoundException.class);
        verify(tripRepository).findByIdWithDays(eq(tripId));
    }

    @Test
    public void unDecidedTripPeriod_initTest() throws Exception {
        // given
        Long tripId = 1L;
        Long tripperId = 2L;

        LocalDate startDate = LocalDate.of(2023, 3, 1);
        LocalDate endDate = LocalDate.of(2023, 3, 3);

        TripPeriodUpdateCommand command = TripPeriodUpdateCommand.of(tripId, tripperId, startDate, endDate);

        Trip trip = TripFixture.undecided_Id(tripId, tripperId);
        given(tripRepository.findByIdWithDays(eq(tripId))).willReturn(Optional.of(trip));

        // when
        tripPeriodUpdateService.updateTripPeriod(command);

        // then
        verify(tripRepository, times(1)).findByIdWithDays(eq(tripId));
        verify(dayRepository, times(1)).saveAll(anyList());
        verify(scheduleRepository, times(0)).relocateDaySchedules(eq(tripId), isNull());
        verify(scheduleRepository, times(0)).moveSchedulesToTemporaryStorage(eq(tripId), anyList());
        verify(dayRepository, times(0)).deleteAllByIds(anyList());
    }

    @Test
    public void 여행_상태가_DECIDED이고_다른_제목과_기간으로_수정하는_경우() throws Exception {
        // given
        long tripId = 1L;
        Long tripperId = 2L;

        LocalDate beforeStartDate = LocalDate.of(2023,3,1);
        LocalDate beforeEndDate = LocalDate.of(2023,3,4);
        LocalDate newStartDate = LocalDate.of(2023,3,2);
        LocalDate newEndDate = LocalDate.of(2023,3,5);

        TripPeriodUpdateCommand command = TripPeriodUpdateCommand.of(tripId, tripperId, newStartDate, newEndDate);

        Trip trip = TripFixture.decided_Id(tripId, tripperId, beforeStartDate, beforeEndDate, 1L);

        given(tripRepository.findByIdWithDays(eq(tripId))).willReturn(Optional.of(trip)); // trip 조회 일어남.
        given(scheduleRepository.relocateDaySchedules(eq(tripId), isNull())).willReturn(0);
        given(scheduleRepository.moveSchedulesToTemporaryStorage(eq(tripId), anyList())).willReturn(0);
        given(dayRepository.deleteAllByIds(anyList())).willReturn(2);

        // when
        tripPeriodUpdateService.updateTripPeriod(command);

        // then
        verify(tripRepository, times(1)).findByIdWithDays(anyLong());
        verify(dayRepository, times(1)).saveAll(anyList()); // 생성된 Day가 있으므로 호출됨
        verify(scheduleRepository, times(1)).relocateDaySchedules(eq(tripId), isNull());
        verify(scheduleRepository, times(1)).moveSchedulesToTemporaryStorage(eq(tripId), anyList());
        verify(dayRepository, times(1)).deleteAllByIds(anyList());
    }

    @Nested
    @DisplayName("여행의 소유자가 아닌 사람이 여행을 수정하려 하면")
    class When_update_tripper_not_equals_trip_owner {

        @Test
        @DisplayName("NoTripUpdateAuthorityException이 발생한다.")
        void it_throws_NoTripUpdateAuthorityException() {
            // given
            Long tripId = 1L;
            Long tripperId = 2L;
            Long noAuthorityTripperId = 3L;

            TripPeriodUpdateCommand command = TripPeriodUpdateCommand.of(tripId, noAuthorityTripperId, null, null);

            Trip trip = TripFixture.undecided_Id(tripId, tripperId);
            given(tripRepository.findByIdWithDays(eq(tripId))).willReturn(Optional.of(trip));

            // when & then
            assertThatThrownBy(() -> tripPeriodUpdateService.updateTripPeriod(command))
                    .isInstanceOf(NoTripUpdateAuthorityException.class);

            verify(tripRepository, times(1)).findByIdWithDays(eq(tripId));
        }

    }

}
