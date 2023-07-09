package com.cosain.trilo.unit.trip.application.schedule.service;

import com.cosain.trilo.fixture.ScheduleFixture;
import com.cosain.trilo.fixture.TripFixture;
import com.cosain.trilo.trip.application.exception.DayNotFoundException;
import com.cosain.trilo.trip.application.exception.NoScheduleMoveAuthorityException;
import com.cosain.trilo.trip.application.exception.ScheduleNotFoundException;
import com.cosain.trilo.trip.application.exception.TooManyDayScheduleException;
import com.cosain.trilo.trip.application.schedule.service.ScheduleMoveService;
import com.cosain.trilo.trip.application.schedule.dto.ScheduleMoveCommand;
import com.cosain.trilo.trip.application.schedule.dto.ScheduleMoveResult;
import com.cosain.trilo.trip.domain.entity.Day;
import com.cosain.trilo.trip.domain.entity.Schedule;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.repository.DayRepository;
import com.cosain.trilo.trip.domain.repository.ScheduleRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static com.cosain.trilo.trip.domain.vo.ScheduleIndex.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
@DisplayName("[TripCommand] ScheduleMoveService 테스트")
public class ScheduleMoveServiceTest {

    @InjectMocks
    private ScheduleMoveService scheduleMoveService;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private DayRepository dayRepository;

    @DisplayName("존재하지 않는 일정을 이동시키려고 하면 ScheduleNotFoundException 발생")
    @Test
    public void testNotExistScheduleMove() {
        // given
        Long notExistScheduleId = 3L;
        Long tripperId = 1L;
        Long targetDayId = 2L;
        int targetOrder = 3;
        ScheduleMoveCommand moveCommand = new ScheduleMoveCommand(targetDayId, targetOrder);

        // mock: scheduleId 조회 -> 해당 일정 존재 안 함
        given(scheduleRepository.findByIdWithTrip(eq(notExistScheduleId))).willReturn(Optional.empty());

        // when & then : 발생 예외 및 리포지토리 호출 횟수 검증
        assertThatThrownBy(() -> scheduleMoveService.moveSchedule(notExistScheduleId, tripperId, moveCommand))
                .isInstanceOf(ScheduleNotFoundException.class);
        verify(scheduleRepository).findByIdWithTrip(eq(notExistScheduleId));
    }

    @DisplayName("존재하지 않는 Day로 이동시키려고 하면, DayNotFoundException 발생")
    @Test
    public void testNotExistTargetDayMove() {
        // given
        Long tripId = 1L;
        Long tripperId = 2L;
        Long targetDayId = 3L;
        Long scheduleId = 4L;
        int targetOrder = 3;
        ScheduleMoveCommand moveCommand = new ScheduleMoveCommand(targetDayId, targetOrder);

        // mock: 이동하고자 하는 Schedule 설정
        Trip trip = TripFixture.undecided_Id(tripId, tripperId);
        Schedule schedule = ScheduleFixture.temporaryStorage_Id(scheduleId, trip, 0);
        given(scheduleRepository.findByIdWithTrip(eq(scheduleId))).willReturn(Optional.of(schedule));

        // mock: targetDayId에 해당하는 Day가 없음
        given(dayRepository.findByIdWithTrip(eq(targetDayId))).willReturn(Optional.empty());

        // when & then : 발생 예외 및 리포지토리 호출횟수 검증
        assertThatThrownBy(() -> scheduleMoveService.moveSchedule(scheduleId, tripperId, moveCommand))
                .isInstanceOf(DayNotFoundException.class);
        verify(scheduleRepository).findByIdWithTrip(eq(scheduleId));
        verify(dayRepository).findByIdWithTrip(eq(targetDayId));
        verify(scheduleRepository, times(0)).findDayScheduleCount(eq(targetDayId));
    }

    @DisplayName("권한이 없는 사람이 이동을 시도하면 NoScheduleMoveAuthorityException 발생")
    @Test
    public void noTripMoveMoveAuthorityTripperTest() {
        // given
        Long tripId = 1L;
        Long scheduleId = 2L;
        Long targetDayId = 3L;
        Long tripperId = 4L;
        Long noAuthorityTripperId = 5L;
        int targetOrder = 0;
        ScheduleMoveCommand moveCommand = new ScheduleMoveCommand(targetDayId, targetOrder);

        // mock : 삭제하고자 하는 Schedule 설정
        LocalDate startDate = LocalDate.of(2023,3,1);
        LocalDate endDate = LocalDate.of(2023,3,1);
        Trip trip = TripFixture.decided_Id(tripId, tripperId, startDate, endDate, targetDayId);
        Day day = trip.getDays().get(0);
        Schedule schedule = ScheduleFixture.day_Id(scheduleId, trip, day, 0);
        given(scheduleRepository.findByIdWithTrip(eq(scheduleId))).willReturn(Optional.of(schedule));

        // mock : targetDayId로 조회시 찾아와지는 Day
        given(dayRepository.findByIdWithTrip(eq(targetDayId))).willReturn(Optional.of(day));

        // when & then : 권한 없는 사용자의 요청 -> 발생 예외 및 리포지토리 호출 횟수 검증
        assertThatThrownBy(() -> scheduleMoveService.moveSchedule(scheduleId, noAuthorityTripperId, moveCommand))
                .isInstanceOf(NoScheduleMoveAuthorityException.class);
        verify(scheduleRepository).findByIdWithTrip(eq(scheduleId));
        verify(dayRepository).findByIdWithTrip(eq(targetDayId));
        verify(scheduleRepository, times(0)).findDayScheduleCount(eq(targetDayId));
    }

    @DisplayName("임시보관함 -> Day 성공 테스트")
    @Test
    public void test_temporaryStorage_to_day_success() {
        // given
        Long tripId = 1L;
        Long scheduleId = 2L;
        Long tripperId = 3L;
        Long targetDayId = 4L;
        int targetOrder = 0;
        LocalDate startDate = LocalDate.of(2023,3,1);
        LocalDate endDate = LocalDate.of(2023,3,1);
        ScheduleMoveCommand moveCommand = new ScheduleMoveCommand(targetDayId, targetOrder);

        // mock: 리포지토리에서 찾아올 Schedule 설정
        Trip trip = TripFixture.decided_Id(tripId, tripperId, startDate, endDate, targetDayId);
        Day day = trip.getDays().get(0);
        Schedule schedule = ScheduleFixture.temporaryStorage_Id(scheduleId, trip, 0);
        given(scheduleRepository.findByIdWithTrip(eq(scheduleId))).willReturn(Optional.of(schedule));

        // mock : 리포지토리에서 찾아올 targetDay
        given(dayRepository.findByIdWithTrip(eq(targetDayId))).willReturn(Optional.of(day));

        // mock : targetDay에 속한 Schedule 갯수
        given(scheduleRepository.findDayScheduleCount(eq(targetDayId))).willReturn(0);

        // when : 서비스에 Schedule을 이동키라고 요청할 때
        var scheduleMoveResult = scheduleMoveService.moveSchedule(scheduleId, tripperId, moveCommand);

        // then : 리포지토리 호출 횟수 및 반환 Dto 필드 검증
        verify(scheduleRepository, times(1)).findByIdWithTrip(eq(scheduleId));
        verify(dayRepository, times(1)).findByIdWithTrip(eq(targetDayId));
        verify(scheduleRepository, times(1)).findDayScheduleCount(eq(targetDayId));
        verify(scheduleRepository, times(0)).relocateDaySchedules(eq(tripId), eq(targetDayId));
        assertThat(scheduleMoveResult.getBeforeDayId()).isEqualTo(null);
        assertThat(scheduleMoveResult.getAfterDayId()).isEqualTo(targetDayId);
        assertThat(scheduleMoveResult.isPositionChanged()).isEqualTo(true);
    }

    @DisplayName("임시보관함 -> 임시보관함 성공 테스트")
    @Test
    public void test_temporaryStorage_to_temporaryStorage_success() {
        // given
        Long tripId = 1L;
        Long tripperId = 1L;
        Long targetDayId = null;
        Long scheduleId = 2L;
        int targetOrder = 2;
        ScheduleMoveCommand moveCommand = new ScheduleMoveCommand(targetDayId, targetOrder);

        // mock: 리포지토리에서 가져올 Schedule 설정
        Trip trip = TripFixture.undecided_Id(tripId, tripperId);
        Schedule schedule1 = ScheduleFixture.temporaryStorage_Id(scheduleId, trip, 0L);
        Schedule schedule2 = ScheduleFixture.temporaryStorage_Id(2L, trip, 100L);
        Schedule schedule3 = ScheduleFixture.temporaryStorage_Id(3L, trip, 200L);
        given(scheduleRepository.findByIdWithTrip(eq(scheduleId))).willReturn(Optional.of(schedule1));

        // when : schedule1 을 2번 위치 Schedule 앞에 이동시켜라
        ScheduleMoveResult scheduleMoveResult = scheduleMoveService.moveSchedule(scheduleId, tripperId, moveCommand);

        // then : 이동 후의 Schedule 순서값, 응답 Dto, 리포지토리 호출 횟수 검증
        assertThat(schedule1.getScheduleIndex()).isEqualTo(schedule2.getScheduleIndex().mid(schedule3.getScheduleIndex()));
        assertThat(scheduleMoveResult.getBeforeDayId()).isNull();
        assertThat(scheduleMoveResult.getAfterDayId()).isNull();
        assertThat(scheduleMoveResult.isPositionChanged()).isEqualTo(true);
        verify(scheduleRepository, times(1)).findByIdWithTrip(eq(scheduleId));
        verify(dayRepository, times(0)).findByIdWithTrip(eq(targetDayId));
        verify(scheduleRepository, times(0)).findDayScheduleCount(eq(targetDayId));
        verify(scheduleRepository, times(0)).relocateDaySchedules(eq(tripId), eq(targetDayId));
    }

    @DisplayName("Day -> 임시보관함 성공 테스트")
    @Test
    public void test_day_to_temporaryStorage_success() {
        // given
        Long tripId = 1L;
        Long scheduleId = 1L;
        Long tripperId = 3L;
        Long fromDayId = 4L;
        Long targetDayId = null;
        int targetOrder = 2;
        ScheduleMoveCommand moveCommand = new ScheduleMoveCommand(targetDayId, targetOrder);

        // mock : Schedule 설정
        LocalDate startDate = LocalDate.of(2023,3,1);
        LocalDate endDate = LocalDate.of(2023,3,1);

        Trip trip = TripFixture.decided_Id(tripId, tripperId, startDate, endDate, fromDayId);
        Day fromDay = trip.getDays().get(0);
        Schedule schedule1 = ScheduleFixture.day_Id(scheduleId, trip, fromDay, 0L);
        Schedule schedule2 = ScheduleFixture.temporaryStorage_Id(2L, trip, 100L);
        Schedule schedule3 = ScheduleFixture.temporaryStorage_Id(3L, trip, 200L);
        given(scheduleRepository.findByIdWithTrip(eq(scheduleId))).willReturn(Optional.of(schedule1));

        // when : 일정을 이동하라(임시보관함의 2번 순서로)
        var scheduleMoveResult = scheduleMoveService.moveSchedule(scheduleId, tripperId, moveCommand);

        // then : 이동 후 Schedule의 소속 Day, 순서값, 응답 Dto, 리포지토리 호출 횟수 검증
        assertThat(schedule1.getDay()).isNull();
        assertThat(schedule1.getScheduleIndex()).isEqualTo(schedule3.getScheduleIndex().generateNextIndex());
        assertThat(scheduleMoveResult.getBeforeDayId()).isEqualTo(fromDayId);
        assertThat(scheduleMoveResult.getAfterDayId()).isNull();
        assertThat(scheduleMoveResult.isPositionChanged()).isEqualTo(true);
        verify(scheduleRepository, times(1)).findByIdWithTrip(eq(scheduleId));
        verify(dayRepository, times(0)).findByIdWithTrip(eq(targetDayId));
        verify(scheduleRepository, times(0)).findDayScheduleCount(eq(targetDayId));
        verify(scheduleRepository, times(0)).relocateDaySchedules(eq(tripId), eq(targetDayId));
    }


    @DisplayName("Day -> 같은 Day 성공 테스트")
    @Test
    public void test_sameDay_success() {
        // given
        Long tripId = 1L;
        Long scheduleId = 1L;
        Long tripperId = 3L;
        Long targetDayId = 4L;
        int targetOrder = 2;
        LocalDate startDate = LocalDate.of(2023,3,1);
        LocalDate endDate = LocalDate.of(2023,3,1);
        ScheduleMoveCommand moveCommand = new ScheduleMoveCommand(targetDayId, targetOrder);

        // mock : 찾아올 Schedule 및 소속 Trip, Day 설정
        Trip trip = TripFixture.decided_Id(tripId, tripperId, startDate, endDate, targetDayId);
        Day day = trip.getDays().get(0);
        Schedule schedule1 = ScheduleFixture.day_Id(scheduleId, trip, day, 0);
        Schedule schedule2 = ScheduleFixture.day_Id(2L, trip, day, 100L);
        Schedule schedule3 = ScheduleFixture.day_Id(3L, trip, day, 200L);
        given(scheduleRepository.findByIdWithTrip(eq(scheduleId))).willReturn(Optional.of(schedule1));

        // mock : targetDayId로 찾아올 day(같은 Day)
        given(dayRepository.findByIdWithTrip(eq(targetDayId))).willReturn(Optional.of(day));

        // when : 일정 1번을 같은 Day의 2번 위치 앞에 둬라
        ScheduleMoveResult scheduleMoveResult = scheduleMoveService.moveSchedule(scheduleId, tripperId, moveCommand);

        // then : 이동 후 Schedule의 소속 Day, 순서값, 응답 Dto, 리포지토리 호출 횟수 검증
        assertThat(schedule1.getScheduleIndex()).isEqualTo(schedule2.getScheduleIndex().mid(schedule3.getScheduleIndex()));
        assertThat(scheduleMoveResult.getBeforeDayId()).isEqualTo(targetDayId);
        assertThat(scheduleMoveResult.getAfterDayId()).isEqualTo(targetDayId);
        assertThat(scheduleMoveResult.isPositionChanged()).isEqualTo(true);
        verify(scheduleRepository, times(1)).findByIdWithTrip(eq(scheduleId));
        verify(dayRepository, times(1)).findByIdWithTrip(eq(targetDayId));
        verify(scheduleRepository, times(0)).findDayScheduleCount(eq(targetDayId));
        verify(scheduleRepository, times(0)).relocateDaySchedules(eq(tripId), eq(targetDayId));
    }

    @DisplayName("Day -> 다른 Day 성공 테스트")
    @Test
    public void test_day_to_other_day_success() {
        // given
        Long tripId = 1L;
        Long scheduleId = 1L;
        Long tripperId = 2L;
        Long fromDayId = 4L;
        Long targetDayId = 5L;
        int targetOrder = 0;
        ScheduleMoveCommand moveCommand = new ScheduleMoveCommand(targetDayId, targetOrder);

        // mock : 리포지토리에서 가져올 Schedule 및 소속 Trip, Day 설정
        LocalDate startDate = LocalDate.of(2023,3,1);
        LocalDate endDate = LocalDate.of(2023,3,2);

        Trip trip = TripFixture.decided_Id(tripId, tripperId, startDate, endDate, fromDayId);
        Day fromDay = trip.getDays().get(0);
        Day targetDay = trip.getDays().get(1);

        Schedule schedule1 = ScheduleFixture.day_Id(scheduleId, trip, fromDay, 0L);
        Schedule schedule2 = ScheduleFixture.day_Id(scheduleId, trip, targetDay, 0L);
        Schedule schedule3 = ScheduleFixture.day_Id(scheduleId, trip, targetDay, 100L);
        given(scheduleRepository.findByIdWithTrip(eq(scheduleId))).willReturn(Optional.of(schedule1));

        // mock : targetDayId에 대응하는 Day 조회
        given(dayRepository.findByIdWithTrip(eq(targetDayId))).willReturn(Optional.of(targetDay));

        // mock : targetDay에 소속된 Schedule 갯수
        given(scheduleRepository.findDayScheduleCount(eq(targetDayId))).willReturn(2);

        // when : schedule1을 targetDay의 0번 순서 앞에 이동시켜라
        ScheduleMoveResult scheduleMoveResult = scheduleMoveService.moveSchedule(scheduleId, tripperId, moveCommand);

        // then : 이동 후 Schedule의 소속 Day, 순서값, 응답 Dto, 리포지토리 호출 횟수 검증
        assertThat(schedule1.getDay().getId()).isEqualTo(targetDayId);
        assertThat(schedule1.getScheduleIndex()).isEqualTo(schedule2.getScheduleIndex().generateBeforeIndex());
        assertThat(scheduleMoveResult.getBeforeDayId()).isEqualTo(fromDayId);
        assertThat(scheduleMoveResult.getAfterDayId()).isEqualTo(targetDayId);
        assertThat(scheduleMoveResult.isPositionChanged()).isEqualTo(true);
        verify(scheduleRepository, times(1)).findByIdWithTrip(eq(scheduleId));
        verify(dayRepository, times(1)).findByIdWithTrip(eq(targetDayId));
        verify(scheduleRepository, times(1)).findDayScheduleCount(eq(targetDayId));
        verify(scheduleRepository, times(0)).relocateDaySchedules(eq(tripId), eq(targetDayId));
    }

    @DisplayName("맨 뒤에 이동 시 인덱스 범위 벗어나면 재배치 후 리포지토리 호출이 추가적으로 발생")
    @Test
    public void testRelocate_whenMoveToTail() {
        // given
        Long tripId = 1L;
        Long scheduleId = 1L;
        Long tripperId = 3L;
        Long targetDayId = 4L;
        int targetOrder = 1;
        ScheduleMoveCommand moveCommand = new ScheduleMoveCommand(targetDayId, targetOrder);

        // mock : 재배치 이전의 Schedule 설정(Trip, Day 포함)
        LocalDate startDate = LocalDate.of(2023,3,1);
        LocalDate endDate = LocalDate.of(2023,3,1);

        Trip beforeTrip = TripFixture.decided_Id(tripId, tripperId, startDate, endDate, targetDayId);
        Schedule beforeMoveSchedule = ScheduleFixture.temporaryStorage_Id(scheduleId, beforeTrip, 0L);
        Day beforeTargetDay = beforeTrip.getDays().get(0);
        Schedule beforeTargetDaySchedule = ScheduleFixture.day_Id(2L, beforeTrip, beforeTargetDay, MAX_INDEX_VALUE);

        // mock : 재배치 이후 다시 가져올 Schedule 설정(Trip, Day 포함)
        Trip rediscoveredTrip = TripFixture.decided_Id(tripId, tripperId, startDate, endDate, targetDayId);
        Day rediscoveredTargetDay = rediscoveredTrip.getDays().get(0);
        Schedule rediscoveredMoveSchedule = ScheduleFixture.temporaryStorage_Id(scheduleId, rediscoveredTrip, 0L);
        Schedule rediscoveredTargetDaySchedule = ScheduleFixture.day_Id(2L, rediscoveredTrip, rediscoveredTargetDay, 0L);

        when(scheduleRepository.findByIdWithTrip(eq(scheduleId)))
                .thenReturn(Optional.of(beforeMoveSchedule))
                .thenReturn(Optional.of(rediscoveredMoveSchedule));

        when(dayRepository.findByIdWithTrip(eq(targetDayId)))
                .thenReturn(Optional.of(beforeTargetDay))
                .thenReturn(Optional.of(rediscoveredTargetDay));

        given(scheduleRepository.relocateDaySchedules(eq(tripId), eq(targetDayId))).willReturn(1);
        given(scheduleRepository.findDayScheduleCount(eq(targetDayId))).willReturn(1);

        // when : schedule1을 targetDay의 1번 순서로 이동하라
        ScheduleMoveResult scheduleMoveResult = scheduleMoveService.moveSchedule(scheduleId, tripperId, moveCommand);

        // then : 이동 후 Schedule의 소속 Day, 순서값, 응답 Dto, 리포지토리 호출 횟수 검증
        assertThat(rediscoveredMoveSchedule.getDay().getId()).isEqualTo(targetDayId);
        assertThat(rediscoveredMoveSchedule.getScheduleIndex()).isEqualTo(rediscoveredTargetDaySchedule.getScheduleIndex().generateNextIndex());
        assertThat(scheduleMoveResult.getBeforeDayId()).isEqualTo(null);
        assertThat(scheduleMoveResult.getAfterDayId()).isEqualTo(targetDayId);
        assertThat(scheduleMoveResult.isPositionChanged()).isEqualTo(true);
        verify(scheduleRepository, times(2)).findByIdWithTrip(eq(scheduleId));
        verify(dayRepository, times(2)).findByIdWithTrip(eq(targetDayId));
        verify(scheduleRepository, times(1)).findDayScheduleCount(eq(targetDayId));
        verify(scheduleRepository, times(1)).relocateDaySchedules(eq(tripId), eq(targetDayId));
    }

    @DisplayName("맨 앞에 이동 시 인덱스 범위 벗어나면 재배치 후 리포지토리 호출이 추가적으로 발생")
    @Test
    public void testRelocate_whenMoveToHead() {
        // given
        Long tripId = 1L;
        Long scheduleId = 1L;
        Long tripperId = 3L;
        Long targetDayId = 4L;
        int targetOrder = 0;
        ScheduleMoveCommand moveCommand = new ScheduleMoveCommand(targetDayId, targetOrder);

        // mock : 재배치 이전의 Schedule(+ Trip, Day) 설정
        LocalDate startDate = LocalDate.of(2023,3,1);
        LocalDate endDate = LocalDate.of(2023,3,1);

        Trip beforeTrip = TripFixture.decided_Id(tripId, tripperId, startDate, endDate, targetDayId);
        Day beforeTargetDay = beforeTrip.getDays().get(0);
        Schedule beforeMoveSchedule = ScheduleFixture.temporaryStorage_Id(scheduleId, beforeTrip, 0L);
        Schedule beforeTargetDaySchedule = ScheduleFixture.day_Id(2L, beforeTrip, beforeTargetDay, MIN_INDEX_VALUE);

        // mock : 재배치 이후 다시 가져올 Schedule(+ Trip, Day) 설정
        Trip rediscoveredTrip = TripFixture.decided_Id(tripId, tripperId, startDate, endDate, targetDayId);
        Day rediscoveredTargetDay = rediscoveredTrip.getDays().get(0);
        Schedule rediscoveredMoveSchedule = ScheduleFixture.temporaryStorage_Id(scheduleId, rediscoveredTrip, 0L);
        Schedule rediscoveredTargetDaySchedule = ScheduleFixture.day_Id(2L, rediscoveredTrip, rediscoveredTargetDay, 0L);

        when(scheduleRepository.findByIdWithTrip(eq(scheduleId)))
                .thenReturn(Optional.of(beforeMoveSchedule))
                .thenReturn(Optional.of(rediscoveredMoveSchedule));

        when(dayRepository.findByIdWithTrip(eq(targetDayId)))
                .thenReturn(Optional.of(beforeTargetDay))
                .thenReturn(Optional.of(rediscoveredTargetDay));

        given(scheduleRepository.findDayScheduleCount(eq(targetDayId))).willReturn(1);
        given(scheduleRepository.relocateDaySchedules(eq(tripId), eq(targetDayId))).willReturn(1);

        // when : schedule을 targetDay의 0번 순서 앞에 이동시켜
        ScheduleMoveResult scheduleMoveResult = scheduleMoveService.moveSchedule(scheduleId, tripperId, moveCommand);

        // then : 이동 후 Schedule의 소속 Day, 순서값, 응답 Dto, 리포지토리 호출 횟수 검증
        assertThat(rediscoveredMoveSchedule.getDay().getId()).isEqualTo(targetDayId);
        assertThat(rediscoveredMoveSchedule.getScheduleIndex()).isEqualTo(rediscoveredTargetDaySchedule.getScheduleIndex().generateBeforeIndex());
        assertThat(scheduleMoveResult.getBeforeDayId()).isEqualTo(null);
        assertThat(scheduleMoveResult.getAfterDayId()).isEqualTo(targetDayId);
        assertThat(scheduleMoveResult.isPositionChanged()).isEqualTo(true);
        verify(scheduleRepository, times(2)).findByIdWithTrip(eq(scheduleId));
        verify(dayRepository, times(2)).findByIdWithTrip(eq(targetDayId));
        verify(scheduleRepository, times(1)).findDayScheduleCount(eq(targetDayId));
        verify(scheduleRepository, times(1)).relocateDaySchedules(eq(tripId), eq(targetDayId));
    }

    @DisplayName("중간 이동 시 충돌나면 재배치 후 리포지토리 호출이 추가적으로 발생")
    @Test
    public void testRelocate_whenMoveToMiddle() {
        // given
        Long tripId = 1L;
        Long scheduleId = 2L;
        Long tripperId = 3L;
        Long targetDayId = 4L;
        int targetOrder = 1;
        ScheduleMoveCommand moveCommand = new ScheduleMoveCommand(targetDayId, targetOrder);

        // mock: Schedule 및 소속 Trip, Day 설정
        LocalDate startDate = LocalDate.of(2023,3,1);
        LocalDate endDate = LocalDate.of(2023,3,1);

        Trip beforeTrip = TripFixture.decided_Id(tripId, tripperId, startDate, endDate, targetDayId);
        Day beforeTargetDay = beforeTrip.getDays().get(0);
        Schedule beforeMoveSchedule = ScheduleFixture.temporaryStorage_Id(scheduleId, beforeTrip, 0L);
        Schedule beforeTargetDaySchedule1 = ScheduleFixture.day_Id(scheduleId, beforeTrip, beforeTargetDay, 10L);
        Schedule beforeTargetDaySchedule2 = ScheduleFixture.day_Id(scheduleId, beforeTrip, beforeTargetDay, 11L);

        // mock : 재배치 이후 다시 가져올 Schedule 및 소속 Trip, Day 설정
        Trip rediscoveredTrip = TripFixture.decided_Id(tripId, tripperId, startDate, endDate, targetDayId);
        Day rediscoveredTargetDay = rediscoveredTrip.getDays().get(0);
        Schedule rediscoveredMoveSchedule = ScheduleFixture.temporaryStorage_Id(scheduleId, rediscoveredTrip, 0L);
        Schedule rediscoveredTargetDaySchedule1 = ScheduleFixture.day_Id(scheduleId, rediscoveredTrip, rediscoveredTargetDay, 0L);
        Schedule rediscoveredTargetDaySchedule2 = ScheduleFixture.day_Id(scheduleId, rediscoveredTrip, rediscoveredTargetDay, DEFAULT_SEQUENCE_GAP);

        when(scheduleRepository.findByIdWithTrip(eq(scheduleId)))
                .thenReturn(Optional.of(beforeMoveSchedule))
                .thenReturn(Optional.of(rediscoveredMoveSchedule));

        when(dayRepository.findByIdWithTrip(eq(targetDayId)))
                .thenReturn(Optional.of(beforeTargetDay))
                .thenReturn(Optional.of(rediscoveredTargetDay));

        given(scheduleRepository.findDayScheduleCount(eq(targetDayId))).willReturn(2);
        given(scheduleRepository.relocateDaySchedules(eq(tripId), eq(targetDayId))).willReturn(1);

        // when
        ScheduleMoveResult scheduleMoveResult = scheduleMoveService.moveSchedule(scheduleId, tripperId, moveCommand);

        // then
        assertThat(scheduleMoveResult.getBeforeDayId()).isEqualTo(null);
        assertThat(scheduleMoveResult.getAfterDayId()).isEqualTo(targetDayId);
        assertThat(scheduleMoveResult.isPositionChanged()).isEqualTo(true);
        verify(scheduleRepository, times(2)).findByIdWithTrip(eq(scheduleId));
        verify(dayRepository, times(2)).findByIdWithTrip(eq(targetDayId));
        verify(scheduleRepository, times(1)).findDayScheduleCount(eq(targetDayId));
        verify(scheduleRepository, times(1)).relocateDaySchedules(eq(tripId), eq(targetDayId));
    }

    @DisplayName("기존과 다른 Day로 이동 -> 일정이 가득차 있으면 예외 발생")
    @Test
    public void testOtherTargetDay_is_Full_Schedule() {
        // given
        Long tripId = 1L;
        Long scheduleId = 2L;
        Long tripperId = 3L;
        Long targetDayId = 4L;
        int targetOrder = 0;
        ScheduleMoveCommand moveCommand = new ScheduleMoveCommand(targetDayId, targetOrder);

        // mock: Schedule 및 소속 Trip, Day 설정
        LocalDate startDate = LocalDate.of(2023,3,1);
        LocalDate endDate = LocalDate.of(2023,3,1);

        Trip trip = TripFixture.decided_Id(tripId, tripperId, startDate, endDate, targetDayId);
        Day targetDay = trip.getDays().get(0);
        Schedule moveSchedule = ScheduleFixture.temporaryStorage_Id(scheduleId, trip, 0L);
        given(scheduleRepository.findByIdWithTrip(eq(scheduleId))).willReturn(Optional.of(moveSchedule));

        // mock: targetDay
        given(dayRepository.findByIdWithTrip(eq(targetDayId))).willReturn(Optional.of(targetDay));

        // targetDay에 일정이 가득찬 상황을 가정
        given(scheduleRepository.findDayScheduleCount(eq(targetDayId))).willReturn(Day.MAX_DAY_SCHEDULE_COUNT);

        // when && then : 발생 오류 및 리포지토리 호출 횟수 검증
        assertThatThrownBy(() -> scheduleMoveService.moveSchedule(scheduleId, tripperId, moveCommand))
                .isInstanceOf(TooManyDayScheduleException.class);
        verify(scheduleRepository, times(1)).findByIdWithTrip(eq(scheduleId));
        verify(dayRepository, times(1)).findByIdWithTrip(eq(targetDayId));
        verify(scheduleRepository, times(1)).findDayScheduleCount(eq(targetDayId));
        verify(scheduleRepository, times(0)).relocateDaySchedules(eq(tripId), eq(targetDayId));
    }

}
