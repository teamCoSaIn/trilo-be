package com.cosain.trilo.unit.trip.application.trip.service.trip_delete;

import com.cosain.trilo.common.exception.trip.NoTripDeleteAuthorityException;
import com.cosain.trilo.common.exception.trip.TripNotFoundException;
import com.cosain.trilo.fixture.TripFixture;
import com.cosain.trilo.trip.application.trip.service.trip_delete.TripDeleteService;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.repository.DayRepository;
import com.cosain.trilo.trip.domain.repository.ScheduleRepository;
import com.cosain.trilo.trip.domain.repository.TripRepository;
import org.junit.jupiter.api.DisplayName;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * 여행 삭제를 담당하는 여행 삭제 서비스({@link TripDeleteService})의 테스트 코드입니다.
 * @see TripDeleteService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("여행 삭제 서비스 테스트")
public class TripDeleteServiceTest {

    /**
     * 테스트 대상이 되는 여행 삭제 서비스입니다.
     */
    @InjectMocks
    private TripDeleteService tripDeleteService;

    /**
     * TripDeleteService의 의존성
     */
    @Mock
    private TripRepository tripRepository;

    /**
     * TripDeleteService의 의존성
     */
    @Mock
    private DayRepository dayRepository;

    /**
     * TripDeleteService의 의존성
     */
    @Mock
    private ScheduleRepository scheduleRepository;

    /**
     * <p>여행 삭제 요청을 했을 때, 서비스 내부적으로 의도한 대로 동작하는 지 검증합니다.</p>
     * <ul>
     *     <li>여행의 소유자가 실제 존재하는 여행을 삭제하라 요청했으므로 별다른 예외가 발생하지 않습니다.</li>
     *     <li>의존성이 잘 호출되어야 합니다</li>
     * </ul>
     */
    @Test
    @DisplayName("정상 삭제 요청이 들어왔을 때, 리포지토리가 정상적으로 호출되는 지 여부 테스트")
    public void deleteSuccess_and_Repository_Called_Test() {
        // given
        Long tripId = 1L;
        Long requestTripperId = 2L;

        Trip trip = TripFixture.undecided_Id(tripId, requestTripperId);
        given(tripRepository.findById(eq(tripId))).willReturn(Optional.of(trip)); // 리포지토리에서 여행 조회시 사용자의 여행이 조회됨

        // when
        tripDeleteService.deleteTrip(tripId, requestTripperId); // 여행 소유자가 여행 삭제 요청

        // then
        verify(tripRepository, times(1)).findById(eq(tripId));
        verify(scheduleRepository, times(1)).deleteAllByTripId(eq(tripId));
        verify(dayRepository, times(1)).deleteAllByTripId(eq(tripId));
        verify(tripRepository, times(1)).delete(any(Trip.class)); // 의존성 호출 검증
    }

    /**
     * <p>존재하지 않는 여행삭제 요청을 했을 때 예외가 발생되는 지 검증합니다.</p>
     * <ul>
     *     <li>{@link TripNotFoundException} 예외가 발생해야합니다.</li>
     *     <li>여행을 조회하는 부분까지만 의존성이 호출되어야 합니다.</li>
     * </ul>
     * @see TripNotFoundException
     */
    @Test
    @DisplayName("존재하지 않는 여행을 삭제하려 하면, TripNotFoundException 발생")
    public void if_delete_not_exist_trip_then_it_throws_TripNotFoundException() {
        // given
        Long tripId = 1L;
        Long tripperId = 1L;
        given(tripRepository.findById(eq(tripId))).willReturn(Optional.empty()); // 리포지토리에서 여행 빈 Optional이 반환됨

        // when & then
        assertThatThrownBy(() -> tripDeleteService.deleteTrip(tripId, tripperId))
                .isInstanceOf(TripNotFoundException.class); // 존재하지 않는 여행 삭제 요청 -> 예외 발생 검증

        verify(tripRepository, times(1)).findById(eq(tripId));
        verify(scheduleRepository, times(0)).deleteAllByTripId(eq(tripId));
        verify(dayRepository, times(0)).deleteAllByTripId(eq(tripId));
        verify(tripRepository, times(0)).delete(any(Trip.class)); // 의존성 호출 검증
    }

    /**
     * <p>권한이 없는 사용자가 여행삭제 요청을 했을 때 예외가 발생되는 지 검증합니다.</p>
     * <ul>
     *     <li>{@link NoTripDeleteAuthorityException} 예외가 발생해야합니다.</li>
     *     <li>여행을 조회하는 부분까지만 의존성이 호출되어야 합니다.</li>
     * </ul>
     * @see NoTripDeleteAuthorityException
     */
    @Test
    @DisplayName("다른 사람이 여행 삭제 요청 -> 예외 발생")
    public void noTripDeleteAuthorityTest() {
        // given
        Long tripId = 1L;
        Long tripOwnerId = 1L;
        Long invalidTripperId = 2L;

        Trip trip = TripFixture.undecided_Id(tripId, tripOwnerId);
        given(tripRepository.findById(eq(tripId))).willReturn(Optional.of(trip));

        // when & then
        assertThatThrownBy(() -> tripDeleteService.deleteTrip(tripId, invalidTripperId)) // 여행 소유자가 아님 -> 예외 발생
                .isInstanceOf(NoTripDeleteAuthorityException.class);

        verify(tripRepository, times(1)).findById(eq(tripId));
        verify(scheduleRepository, times(0)).deleteAllByTripId(eq(tripId));
        verify(dayRepository, times(0)).deleteAllByTripId(eq(tripId));
        verify(tripRepository, times(0)).delete(any(Trip.class)); // 의존성 호출 검증
    }

}
