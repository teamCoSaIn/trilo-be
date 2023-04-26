package com.cosain.trilo.unit.trip.command.application.service;

import com.cosain.trilo.trip.command.application.command.TripUpdateCommand;
import com.cosain.trilo.trip.command.application.exception.NoTripUpdateAuthorityException;
import com.cosain.trilo.trip.command.application.exception.TripNotFoundException;
import com.cosain.trilo.trip.command.application.service.TripUpdateService;
import com.cosain.trilo.trip.command.domain.entity.Trip;
import com.cosain.trilo.trip.command.domain.repository.DayRepository;
import com.cosain.trilo.trip.command.domain.repository.TripRepository;
import com.cosain.trilo.trip.command.domain.vo.TripPeriod;
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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
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

    @Test
    @DisplayName("존재하지 않는 여행을 수정하려 하면, TripNotFoundException 발생")
    public void if_update_not_exist_trip_then_it_throws_TripNotFoundException() {
        // given
        TripUpdateCommand updateCommand =
                TripUpdateCommand.of(
                        "수정할 제목",
                        TripPeriod.of(LocalDate.of(2023,5,5), LocalDate.of(2023, 5, 15))
                );
        given(tripRepository.findByIdWithDays(anyLong())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> tripUpdateService.updateTrip(1L, 1L, updateCommand))
                .isInstanceOf(TripNotFoundException.class);
        verify(tripRepository).findByIdWithDays(anyLong());
    }

    @Test
    public void 여행_상태가_UNDECIDED이고_날짜와_제목을_수정할_때() throws Exception {
        // given
        TripUpdateCommand updateCommand =
                TripUpdateCommand.of(
                        "수정할 제목",
                        TripPeriod.of(LocalDate.of(2023,5,5), LocalDate.of(2023, 5, 15))
                );

        Trip trip = UNDECIDED_TRIP.createUndecided(1L, 1L, "여행 제목");
        given(tripRepository.findByIdWithDays(anyLong())).willReturn(Optional.of(trip));

        // when
        tripUpdateService.updateTrip(1L, 1L, updateCommand);

        // then
        verify(tripRepository, times(1)).findByIdWithDays(anyLong());
        verify(dayRepository, times(0)).deleteDays(anyList());
        verify(dayRepository, times(1)).saveAll(anyList());
    }

    @Test
    public void 여행_상태가_DECIDED이고_다른_제목과_기간으로_수정하는_경우() throws Exception {
        // given
        TripUpdateCommand updateCommand =
                TripUpdateCommand.of(
                        "수정할 제목",
                        TripPeriod.of(LocalDate.of(2023,5,5), LocalDate.of(2023, 5, 15))
                );
        Trip trip = DECIDED_TRIP.createDecided(1L, 1L, "여행 제목", LocalDate.of(2023, 5, 10), LocalDate.of(2023, 5, 20));
        given(tripRepository.findByIdWithDays(anyLong())).willReturn(Optional.of(trip));

        // when
        tripUpdateService.updateTrip(1L, 1L, updateCommand);

        // then
        verify(tripRepository, times(1)).findByIdWithDays(anyLong());
        verify(dayRepository, times(1)).deleteDays(anyList());
        verify(dayRepository, times(1)).saveAll(anyList());
    }

    @Nested
    @DisplayName("여행의 소유자가 아닌 사람이 여행을 수정하려 하면")
    class When_update_tripper_not_equals_trip_owner {

        @Test
        @DisplayName("NoTripUpdateAuthorityException이 발생한다.")
        void it_throws_NoTripUpdateAuthorityException() {
            // given
            TripUpdateCommand updateCommand =
                    TripUpdateCommand.of(
                            "수정할 제목",
                            TripPeriod.of(LocalDate.of(2023,5,5), LocalDate.of(2023, 5, 15))
                    );
            Trip trip = UNDECIDED_TRIP.createUndecided(1L, 1L, "여행 제목");
            given(tripRepository.findByIdWithDays(anyLong())).willReturn(Optional.of(trip));

            // when & then
            assertThatThrownBy(() -> tripUpdateService.updateTrip(1L, 2L, updateCommand))
                    .isInstanceOf(NoTripUpdateAuthorityException.class);

            verify(tripRepository, times(1)).findByIdWithDays(anyLong());
        }

    }

}
