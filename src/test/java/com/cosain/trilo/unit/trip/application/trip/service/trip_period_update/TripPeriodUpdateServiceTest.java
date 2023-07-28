package com.cosain.trilo.unit.trip.application.trip.service.trip_period_update;

import com.cosain.trilo.common.exception.trip.NoTripUpdateAuthorityException;
import com.cosain.trilo.common.exception.trip.TripNotFoundException;
import com.cosain.trilo.fixture.TripFixture;
import com.cosain.trilo.trip.application.trip.service.trip_period_update.TripPeriodUpdateCommand;
import com.cosain.trilo.trip.application.trip.service.trip_period_update.TripPeriodUpdateService;
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

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * 여행 기간수정 서비스({@link TripPeriodUpdateService})의 테스트 클래스입니다.
 * @see TripPeriodUpdateService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("[TripCommand] TripPeriodUpdateService 테스트")
public class TripPeriodUpdateServiceTest {

    /**
     * 테스트 대상이 되는 여행기간 수정 서비스입니다.
     */
    @InjectMocks
    private TripPeriodUpdateService tripPeriodUpdateService;

    /**
     * TripPeriodUpdateService의 의존성
     */
    @Mock
    private TripRepository tripRepository;

    /**
     * TripPeriodUpdateService의 의존성
     */
    @Mock
    private DayRepository dayRepository;

    /**
     * TripPeriodUpdateService의 의존성
     */
    @Mock
    private ScheduleRepository scheduleRepository;

    /**
     * 기간이 정해진 여행을 다른 날짜 기간으로 수정하는 경우에 대한 테스트입니다.
     * <ul>
     *     <li>내부 의존성이 잘 호출됐는 지 검증해야합니다.</li>
     * </ul>
     */
    @Test
    public void testDecidedTripPeriodToOtherTripPeriod() throws Exception {
        // given
        long tripId = 1L;
        Long tripperId = 2L;

        LocalDate beforeStartDate = LocalDate.of(2023,3,1);
        LocalDate beforeEndDate = LocalDate.of(2023,3,4);
        LocalDate newStartDate = LocalDate.of(2023,3,2);
        LocalDate newEndDate = LocalDate.of(2023,3,5);

        var command = TripPeriodUpdateCommand.of(tripId, tripperId, newStartDate, newEndDate);

        Trip trip = TripFixture.decided_Id(tripId, tripperId, beforeStartDate, beforeEndDate, 1L);

        given(tripRepository.findByIdWithDays(eq(tripId))).willReturn(Optional.of(trip)); // trip 조회 일어남.
        given(scheduleRepository.relocateDaySchedules(eq(tripId), isNull())).willReturn(0);
        given(scheduleRepository.moveSchedulesToTemporaryStorage(eq(tripId), anyList())).willReturn(0);
        given(dayRepository.deleteAllByIds(anyList())).willReturn(2);

        // when
        tripPeriodUpdateService.updateTripPeriod(command);

        // then
        // 여행 기간 수정을 위해 여행이 조회됨
        verify(tripRepository, times(1)).findByIdWithDays(eq(tripId));

        // 생성된 Day가 있으므로 재배치 기능 호출됨
        verify(dayRepository, times(1)).saveAll(anyList());

        // Day 삭제되고 일정들의 임시보관함 이동을 위해 임시보관함 재배치가 일어남
        verify(scheduleRepository, times(1)).relocateDaySchedules(eq(tripId), isNull());

        // 일정들이 일괄적으로 임시보관함으로 이동됨
        verify(scheduleRepository, times(1)).moveSchedulesToTemporaryStorage(eq(tripId), anyList());

        // 새로운 기간에 속하지 않는 Day들이 일괄 삭제됨
        verify(dayRepository, times(1)).deleteAllByIds(anyList());
    }

    /**
     * 초기화되지 않은 여행을 새로운 기간으로 수정(초기화)할 때에 대한 테스트입니다.
     * <ul>
     *     <li>내부 의존성이 잘 호출됐는 지 검증해야합니다.</li>
     * </ul>
     */
    @Test
    @DisplayName("기간이 정해지지 않은 여행의 기간을 새로운 기간으로 수정 -> 기간 수정됨")
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

        // 수정할 여행에 대한 조회가 일어남.
        verify(tripRepository, times(1)).findByIdWithDays(eq(tripId));

        // Day 생성을 위해 리포지토리가 호출됨
        verify(dayRepository, times(1)).saveAll(anyList());

        // 삭제되는 Day가 없으므로 임시보관함 재배치는 일어나지 않음
        verify(scheduleRepository, times(0)).relocateDaySchedules(eq(tripId), isNull());

        // 삭제되는 Day 자체가 없으므로 임시보관함으로의 일정 이동도 일어나지 않음
        verify(scheduleRepository, times(0)).moveSchedulesToTemporaryStorage(eq(tripId), anyList());

        // 삭제되는 Day들이 없음
        verify(dayRepository, times(0)).deleteAllByIds(anyList());
    }

    /**
     * <p>존재하지 않는 여행기간 수정 요청을 했을 때 예외가 발생되는 지 검증합니다.</p>
     * <ul>
     *     <li>{@link TripNotFoundException} 예외가 발생해야합니다.</li>
     *     <li>여행을 조회하는 부분까지만 의존성이 호출되어야 합니다.</li>
     * </ul>
     * @see TripNotFoundException
     */
    @Test
    @DisplayName("존재하지 않는 여행의 기간을 수정하려 하면, TripNotFoundException 발생")
    public void if_update_not_exist_trip_then_it_throws_TripNotFoundException() {
        // given
        Long tripId = 1L;
        Long tripperId = 2L;

        LocalDate startDate = LocalDate.of(2023, 3, 1);
        LocalDate endDate = LocalDate.of(2023, 3, 3);

        var command = TripPeriodUpdateCommand.of(tripId, tripperId, startDate, endDate);
        given(tripRepository.findByIdWithDays(eq(tripId))).willReturn(Optional.empty()); // 여행 존재 x

        // when & then
        assertThatThrownBy(() -> tripPeriodUpdateService.updateTripPeriod(command))
                .isInstanceOf(TripNotFoundException.class);
        verify(tripRepository, times(1)).findByIdWithDays(eq(tripId));
    }

    /**
     * <p>권한이 없는 사용자가 여행기간 수정 요청을 했을 때 예외가 발생되는 지 검증합니다.</p>
     * <ul>
     *     <li>{@link NoTripUpdateAuthorityException} 예외가 발생해야합니다.</li>
     *     <li>여행을 조회하는 부분까지만 의존성이 호출되어야 합니다.</li>
     * </ul>
     * @see NoTripUpdateAuthorityException
     */
    @Test
    @DisplayName("다른 사람이 여행 기간 수정 요청 -> 예외 발생")
    void noTripUpdateAuthorityTest() {
        // given
        Long tripId = 1L;
        Long tripperId = 2L;
        Long noAuthorityTripperId = 3L;

        // 다른 사람의 여행 기간 수정 요청
        var command = TripPeriodUpdateCommand.of(tripId, noAuthorityTripperId, null, null);

        Trip trip = TripFixture.undecided_Id(tripId, tripperId);
        given(tripRepository.findByIdWithDays(eq(tripId))).willReturn(Optional.of(trip));

        // when & then
        assertThatThrownBy(() -> tripPeriodUpdateService.updateTripPeriod(command))
                .isInstanceOf(NoTripUpdateAuthorityException.class);

        // 여행 조회를 위해 리포지토리 1번 조회됨
        verify(tripRepository, times(1)).findByIdWithDays(eq(tripId));
    }

}
