package com.cosain.trilo.unit.trip.application.trip.command.service;

import com.cosain.trilo.trip.application.trip.command.usecase.dto.TripUpdateCommand;
import com.cosain.trilo.trip.application.exception.NoTripUpdateAuthorityException;
import com.cosain.trilo.trip.application.exception.TripNotFoundException;
import com.cosain.trilo.trip.application.trip.command.service.TripUpdateService;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.repository.DayRepository;
import com.cosain.trilo.trip.domain.repository.ScheduleRepository;
import com.cosain.trilo.trip.domain.repository.TripRepository;
import com.cosain.trilo.trip.domain.vo.TripPeriod;
import com.cosain.trilo.trip.domain.vo.TripTitle;
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
import static com.cosain.trilo.fixture.TripFixture.UNDECIDED_TRIP;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("[TripCommand] TripUpdateService 테스트")
public class TripUpdateServiceTest {

    @InjectMocks
    private TripUpdateService tripUpdateService;

    @Mock
    private TripRepository tripRepository;

    @Mock
    private DayRepository dayRepository;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Test
    @DisplayName("존재하지 않는 여행을 수정하려 하면, TripNotFoundException 발생")
    public void if_update_not_exist_trip_then_it_throws_TripNotFoundException() {
        // given
        Long tripId = 1L;
        Long tripperId = 2L;
        TripUpdateCommand updateCommand = new TripUpdateCommand(
                TripTitle.of("수정할 제목"),
                TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 3))
        );
        given(tripRepository.findByIdWithDays(eq(tripId))).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> tripUpdateService.updateTrip(tripId, tripperId, updateCommand))
                .isInstanceOf(TripNotFoundException.class);
        verify(tripRepository).findByIdWithDays(eq(tripId));
    }

    @Test
    public void 여행_상태가_UNDECIDED이고_날짜와_제목을_수정할_때() throws Exception {
        // given
        Long tripId = 1L;
        Long tripperId = 2L;
        TripUpdateCommand updateCommand = new TripUpdateCommand(
                TripTitle.of("수정할 제목"),
                TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 3))
        );

        Trip trip = UNDECIDED_TRIP.createUndecided(tripId, tripperId, "여행 제목");
        given(tripRepository.findByIdWithDays(eq(tripId))).willReturn(Optional.of(trip));

        // when
        tripUpdateService.updateTrip(tripId, tripperId, updateCommand);

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
        Long tripId = 1L;
        Long tripperId = 2L;

        TripUpdateCommand updateCommand = new TripUpdateCommand(
                TripTitle.of("수정할 제목"),
                TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 5))
        );
        Trip trip = DECIDED_TRIP.createDecided(tripId, tripperId, "여행 제목", LocalDate.of(2023, 3, 3), LocalDate.of(2023, 3, 7));
        given(tripRepository.findByIdWithDays(eq(tripId))).willReturn(Optional.of(trip));
        given(scheduleRepository.relocateDaySchedules(eq(tripId), isNull())).willReturn(0);
        given(scheduleRepository.moveSchedulesToTemporaryStorage(eq(tripId), anyList())).willReturn(0);
        given(dayRepository.deleteAllByIds(anyList())).willReturn(2);

        // when
        tripUpdateService.updateTrip(tripId, tripperId, updateCommand);

        // then
        verify(tripRepository, times(1)).findByIdWithDays(anyLong());
        verify(dayRepository, times(1)).saveAll(anyList());
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

            TripUpdateCommand updateCommand = new TripUpdateCommand(
                    TripTitle.of("수정할 제목"),
                    TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 3))
            );
            Trip trip = UNDECIDED_TRIP.createUndecided(tripId, tripperId, "여행 제목");
            given(tripRepository.findByIdWithDays(eq(tripId))).willReturn(Optional.of(trip));

            // when & then
            assertThatThrownBy(() -> tripUpdateService.updateTrip(tripId, noAuthorityTripperId, updateCommand))
                    .isInstanceOf(NoTripUpdateAuthorityException.class);

            verify(tripRepository, times(1)).findByIdWithDays(eq(tripId));
        }

    }

}
