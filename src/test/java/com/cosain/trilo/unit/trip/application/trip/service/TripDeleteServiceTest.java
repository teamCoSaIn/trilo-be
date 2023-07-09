package com.cosain.trilo.unit.trip.application.trip.service;

import com.cosain.trilo.fixture.TripFixture;
import com.cosain.trilo.trip.application.exception.NoTripDeleteAuthorityException;
import com.cosain.trilo.trip.application.exception.TripNotFoundException;
import com.cosain.trilo.trip.application.trip.service.TripDeleteService;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("[TripCommand] TripDeleteService 테스트")
public class TripDeleteServiceTest {

    @InjectMocks
    private TripDeleteService tripDeleteService;

    @Mock
    private TripRepository tripRepository;

    @Mock
    private DayRepository dayRepository;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Test
    @DisplayName("존재하지 않는 여행을 삭제하려 하면, TripNotFoundException 발생")
    public void if_delete_not_exist_trip_then_it_throws_TripNotFoundException() {
        // given
        Long tripId = 1L;
        Long tripperId = 1L;
        given(tripRepository.findById(eq(tripId))).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> tripDeleteService.deleteTrip(tripId, tripperId))
                .isInstanceOf(TripNotFoundException.class);
        verify(tripRepository).findById(eq(tripId));
    }

    @Nested
    @DisplayName("여행의 소유자가 아닌 사람이 여행을 삭제하려 하면")
    class When_delete_tripper_not_equals_trip_owner {

        @Test
        @DisplayName("NoTripDeleteAuthorityException 이 발생한다.")
        void it_throws_NoTripDeleteAuthorityException() {
            // given
            Long tripId = 1L;
            Long tripOwnerId = 1L;
            Long invalidTripperId = 2L;

            Trip trip = TripFixture.undecided_Id(tripId, tripOwnerId);
            given(tripRepository.findById(eq(tripId))).willReturn(Optional.of(trip));

            // when & then
            assertThatThrownBy(() -> tripDeleteService.deleteTrip(tripId, invalidTripperId))
                    .isInstanceOf(NoTripDeleteAuthorityException.class);

            verify(tripRepository).findById(eq(tripId));
        }
    }

    @Test
    @DisplayName("정상 삭제 요청이 들어왔을 때, 리포지토리가 정상적으로 호출되는 지 여부 테스트")
    public void deleteSuccess_and_Repository_Called_Test() {
        // given
        Long tripId = 1L;
        Long tripOwnerId = 2L;
        Long deleteTripperId = 2L;

        Trip trip = TripFixture.undecided_Id(tripId, tripOwnerId);

        given(tripRepository.findById(eq(tripId))).willReturn(Optional.of(trip));
        willDoNothing().given(scheduleRepository).deleteAllByTripId(eq(tripId));
        willDoNothing().given(dayRepository).deleteAllByTripId(eq(tripId));
        willDoNothing().given(tripRepository).delete(any(Trip.class));

        // when
        tripDeleteService.deleteTrip(tripId, deleteTripperId);

        // then
        verify(tripRepository).findById(eq(tripId));
        verify(scheduleRepository).deleteAllByTripId(eq(tripId));
        verify(dayRepository).deleteAllByTripId(eq(tripId));
        verify(tripRepository).delete(any(Trip.class));
    }

}
