package com.cosain.trilo.unit.trip.application.schedule.command.service;

import com.cosain.trilo.trip.application.schedule.command.usecase.dto.ScheduleMoveCommand;
import com.cosain.trilo.trip.application.exception.DayNotFoundException;
import com.cosain.trilo.trip.application.exception.NoScheduleMoveAuthorityException;
import com.cosain.trilo.trip.application.exception.ScheduleNotFoundException;
import com.cosain.trilo.trip.application.schedule.command.usecase.dto.ScheduleMoveResult;
import com.cosain.trilo.trip.application.schedule.command.service.ScheduleMoveService;
import com.cosain.trilo.trip.domain.entity.Day;
import com.cosain.trilo.trip.domain.entity.Schedule;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.repository.DayRepository;
import com.cosain.trilo.trip.domain.repository.ScheduleRepository;
import com.cosain.trilo.trip.domain.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

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
        given(scheduleRepository.findByIdWithTrip(eq(notExistScheduleId))).willReturn(Optional.empty());


        // when & then
        assertThatThrownBy(() -> scheduleMoveService.moveSchedule(notExistScheduleId, tripperId, moveCommand))
                .isInstanceOf(ScheduleNotFoundException.class);

        verify(scheduleRepository).findByIdWithTrip(eq(notExistScheduleId));
    }

    @DisplayName("존재하지 않는 Day로 이동시키려고 하면, DayNotFoundException 발생")
    @Test
    public void testNotExistTargetDayMove() {
        // given
        Long scheduleId = 1L;
        Long tripperId = 1L;
        Long notExistTargetDayId = 2L;
        int targetOrder = 3;
        Long tripId = 1L;

        Trip trip = Trip.builder()
                .id(tripId)
                .tripperId(tripperId)
                .tripTitle(TripTitle.of("여행 제목"))
                .status(TripStatus.DECIDED)
                .tripPeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 1)))
                .build();

        Schedule schedule = Schedule.builder()
                .id(scheduleId)
                .day(null)
                .trip(trip)
                .scheduleTitle(ScheduleTitle.of("일정 제목"))
                .place(Place.of("장소 식별자", "장소명", Coordinate.of(23.21, 23.24)))
                .scheduleIndex(ScheduleIndex.ZERO_INDEX)
                .build();
        trip.getTemporaryStorage().add(schedule);

        ScheduleMoveCommand moveCommand = new ScheduleMoveCommand(notExistTargetDayId, targetOrder);
        given(scheduleRepository.findByIdWithTrip(eq(scheduleId))).willReturn(Optional.of(schedule));
        given(dayRepository.findByIdWithTrip(eq(notExistTargetDayId))).willReturn(Optional.empty());


        // when & then
        assertThatThrownBy(() -> scheduleMoveService.moveSchedule(scheduleId, tripperId, moveCommand))
                .isInstanceOf(DayNotFoundException.class);

        verify(scheduleRepository).findByIdWithTrip(eq(scheduleId));
        verify(dayRepository).findByIdWithTrip(eq(notExistTargetDayId));
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

        Trip trip = Trip.builder()
                .id(tripId)
                .tripperId(tripperId)
                .tripTitle(TripTitle.of("여행 제목"))
                .status(TripStatus.DECIDED)
                .tripPeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 1)))
                .build();

        Day day = Day.builder()
                .id(targetDayId)
                .tripDate(LocalDate.of(2023, 3, 1))
                .trip(trip)
                .build();
        trip.getDays().add(day);

        Schedule schedule = Schedule.builder()
                .id(scheduleId)
                .day(null)
                .trip(trip)
                .scheduleTitle(ScheduleTitle.of("일정 제목"))
                .place(Place.of("장소 식별자", "장소명", Coordinate.of(23.21, 23.24)))
                .scheduleIndex(ScheduleIndex.ZERO_INDEX)
                .build();
        trip.getTemporaryStorage().add(schedule);

        ScheduleMoveCommand moveCommand = new ScheduleMoveCommand(targetDayId, targetOrder);

        given(scheduleRepository.findByIdWithTrip(eq(scheduleId))).willReturn(Optional.of(schedule));
        given(dayRepository.findByIdWithTrip(eq(targetDayId))).willReturn(Optional.of(day));


        // when & then
        assertThatThrownBy(() -> scheduleMoveService.moveSchedule(scheduleId, noAuthorityTripperId, moveCommand))
                .isInstanceOf(NoScheduleMoveAuthorityException.class);

        verify(scheduleRepository).findByIdWithTrip(eq(scheduleId));
        verify(dayRepository).findByIdWithTrip(eq(targetDayId));
    }

    @DisplayName("정상적인 이동 요청의 경우, 리포지토리 호출이 1회씩 발생한다.")
    @Test
    public void testSuccess() {
        // given
        Long tripId = 1L;
        Long scheduleId = 2L;
        Long tripperId = 3L;

        Long targetDayId = 4L;
        int targetOrder = 0;

        Trip trip = Trip.builder()
                .id(tripId)
                .tripperId(tripperId)
                .tripTitle(TripTitle.of("여행 제목"))
                .status(TripStatus.DECIDED)
                .tripPeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 1)))
                .build();

        Day day = Day.builder()
                .id(targetDayId)
                .tripDate(LocalDate.of(2023, 3, 1))
                .trip(trip)
                .build();
        trip.getDays().add(day);

        Schedule schedule = Schedule.builder()
                .id(scheduleId)
                .day(null)
                .trip(trip)
                .scheduleTitle(ScheduleTitle.of("일정 제목"))
                .place(Place.of("장소 식별자", "장소명", Coordinate.of(23.21, 23.24)))
                .scheduleIndex(ScheduleIndex.ZERO_INDEX)
                .build();
        trip.getTemporaryStorage().add(schedule);

        ScheduleMoveCommand moveCommand = new ScheduleMoveCommand(targetDayId, targetOrder);

        given(scheduleRepository.findByIdWithTrip(eq(scheduleId))).willReturn(Optional.of(schedule));
        given(dayRepository.findByIdWithTrip(eq(targetDayId))).willReturn(Optional.of(day));

        // when
        ScheduleMoveResult scheduleMoveResult = scheduleMoveService.moveSchedule(scheduleId, tripperId, moveCommand);


        // then
        assertThat(scheduleMoveResult.getBeforeDayId()).isEqualTo(null);
        assertThat(scheduleMoveResult.getAfterDayId()).isEqualTo(targetDayId);
        assertThat(scheduleMoveResult.isPositionChanged()).isEqualTo(true);
        verify(scheduleRepository, times(1)).findByIdWithTrip(eq(scheduleId));
        verify(dayRepository, times(1)).findByIdWithTrip(eq(targetDayId));
        verify(scheduleRepository, times(0)).relocateDaySchedules(eq(tripId), eq(targetDayId));
    }

    @DisplayName("맨 뒤에 이동 시 인덱스 범위 벗어나면 재배치 후 리포지토리 호출이 추가적으로 발생")
    @Test
    public void testRelocate_whenMoveToTail() {
        // given
        Long tripId = 1L;
        Long scheduleId = 2L;
        Long tripperId = 3L;

        Long targetDayId = 4L;
        int targetOrder = 1;

        Trip beforeTrip = Trip.builder()
                .id(tripId)
                .tripperId(tripperId)
                .tripTitle(TripTitle.of("여행 제목"))
                .status(TripStatus.DECIDED)
                .tripPeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 1)))
                .build();
        Day beforeTargetDay = Day.builder()
                .id(targetDayId)
                .tripDate(LocalDate.of(2023, 3, 1))
                .trip(beforeTrip)
                .build();
        Schedule beforeTargetDaySchedule = Schedule.builder()
                .id(scheduleId)
                .day(beforeTargetDay)
                .trip(beforeTrip)
                .scheduleTitle(ScheduleTitle.of("일정 제목1"))
                .place(Place.of("장소 식별자1", "장소명2", Coordinate.of(23.21, 23.24)))
                .scheduleIndex(ScheduleIndex.of(ScheduleIndex.MAX_INDEX_VALUE))
                .build();
        Schedule beforeMoveSchedule = Schedule.builder()
                .id(scheduleId)
                .day(null)
                .trip(beforeTrip)
                .scheduleTitle(ScheduleTitle.of("일정 제목2"))
                .place(Place.of("장소 식별자2", "장소명2", Coordinate.of(23.21, 23.24)))
                .scheduleIndex(ScheduleIndex.ZERO_INDEX)
                .build();
        beforeTrip.getDays().add(beforeTargetDay);
        beforeTargetDay.getSchedules().add(beforeTargetDaySchedule);
        beforeTrip.getTemporaryStorage().add(beforeMoveSchedule);

        Trip rediscoveredTrip = Trip.builder()
                .id(tripId)
                .tripperId(tripperId)
                .tripTitle(TripTitle.of("여행 제목"))
                .status(TripStatus.DECIDED)
                .tripPeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 1)))
                .build();
        Day rediscoveredTargetDay = Day.builder()
                .id(targetDayId)
                .tripDate(LocalDate.of(2023, 3, 1))
                .trip(rediscoveredTrip)
                .build();
        Schedule rediscoveredTargetDaySchedule = Schedule.builder()
                .id(scheduleId)
                .day(rediscoveredTargetDay)
                .trip(rediscoveredTrip)
                .scheduleTitle(ScheduleTitle.of("일정 제목1"))
                .place(Place.of("장소 식별자1", "장소명2", Coordinate.of(23.21, 23.24)))
                .scheduleIndex(ScheduleIndex.ZERO_INDEX)
                .build();
        Schedule rediscoveredMoveSchedule = Schedule.builder()
                .id(scheduleId)
                .day(null)
                .trip(rediscoveredTrip)
                .scheduleTitle(ScheduleTitle.of("일정 제목"))
                .place(Place.of("장소 식별자", "장소명", Coordinate.of(23.21, 23.24)))
                .scheduleIndex(ScheduleIndex.ZERO_INDEX)
                .build();
        rediscoveredTrip.getDays().add(rediscoveredTargetDay);
        rediscoveredTargetDay.getSchedules().add(rediscoveredTargetDaySchedule);
        rediscoveredTrip.getTemporaryStorage().add(rediscoveredMoveSchedule);

        ScheduleMoveCommand moveCommand = new ScheduleMoveCommand(targetDayId, targetOrder);


        when(scheduleRepository.findByIdWithTrip(eq(scheduleId)))
                .thenReturn(Optional.of(beforeMoveSchedule))
                .thenReturn(Optional.of(rediscoveredMoveSchedule));

        when(dayRepository.findByIdWithTrip(eq(targetDayId)))
                .thenReturn(Optional.of(beforeTargetDay))
                .thenReturn(Optional.of(rediscoveredTargetDay));

        given(scheduleRepository.relocateDaySchedules(eq(tripId), eq(targetDayId))).willReturn(1);

        // when
        ScheduleMoveResult scheduleMoveResult = scheduleMoveService.moveSchedule(scheduleId, tripperId, moveCommand);

        // then
        assertThat(scheduleMoveResult.getBeforeDayId()).isEqualTo(null);
        assertThat(scheduleMoveResult.getAfterDayId()).isEqualTo(targetDayId);
        assertThat(scheduleMoveResult.isPositionChanged()).isEqualTo(true);
        verify(scheduleRepository, times(2)).findByIdWithTrip(eq(scheduleId));
        verify(dayRepository, times(2)).findByIdWithTrip(eq(targetDayId));
        verify(scheduleRepository, times(1)).relocateDaySchedules(eq(tripId), eq(targetDayId));
    }

    @DisplayName("맨 앞에 이동 시 인덱스 범위 벗어나면 재배치 후 리포지토리 호출이 추가적으로 발생")
    @Test
    public void testRelocate_whenMoveToHead() {
        // given
        Long tripId = 1L;
        Long scheduleId = 2L;
        Long tripperId = 3L;

        Long targetDayId = 4L;
        int targetOrder = 0;

        Trip beforeTrip = Trip.builder()
                .id(tripId)
                .tripperId(tripperId)
                .tripTitle(TripTitle.of("여행 제목"))
                .status(TripStatus.DECIDED)
                .tripPeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 1)))
                .build();
        Day beforeTargetDay = Day.builder()
                .id(targetDayId)
                .tripDate(LocalDate.of(2023, 3, 1))
                .trip(beforeTrip)
                .build();
        Schedule beforeTargetDaySchedule = Schedule.builder()
                .id(scheduleId)
                .day(beforeTargetDay)
                .trip(beforeTrip)
                .scheduleTitle(ScheduleTitle.of("일정 제목1"))
                .place(Place.of("장소 식별자1", "장소명2", Coordinate.of(23.21, 23.24)))
                .scheduleIndex(ScheduleIndex.of(ScheduleIndex.MIN_INDEX_VALUE))
                .build();
        Schedule beforeMoveSchedule = Schedule.builder()
                .id(scheduleId)
                .day(null)
                .trip(beforeTrip)
                .scheduleTitle(ScheduleTitle.of("일정 제목2"))
                .place(Place.of("장소 식별자2", "장소명2", Coordinate.of(23.21, 23.24)))
                .scheduleIndex(ScheduleIndex.ZERO_INDEX)
                .build();
        beforeTrip.getDays().add(beforeTargetDay);
        beforeTargetDay.getSchedules().add(beforeTargetDaySchedule);
        beforeTrip.getTemporaryStorage().add(beforeMoveSchedule);

        Trip rediscoveredTrip = Trip.builder()
                .id(tripId)
                .tripperId(tripperId)
                .tripTitle(TripTitle.of("여행 제목"))
                .status(TripStatus.DECIDED)
                .tripPeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 1)))
                .build();
        Day rediscoveredTargetDay = Day.builder()
                .id(targetDayId)
                .tripDate(LocalDate.of(2023, 3, 1))
                .trip(rediscoveredTrip)
                .build();
        Schedule rediscoveredTargetDaySchedule = Schedule.builder()
                .id(scheduleId)
                .day(rediscoveredTargetDay)
                .trip(rediscoveredTrip)
                .scheduleTitle(ScheduleTitle.of("일정 제목1"))
                .place(Place.of("장소 식별자1", "장소명2", Coordinate.of(23.21, 23.24)))
                .scheduleIndex(ScheduleIndex.ZERO_INDEX)
                .build();
        Schedule rediscoveredMoveSchedule = Schedule.builder()
                .id(scheduleId)
                .day(null)
                .trip(rediscoveredTrip)
                .scheduleTitle(ScheduleTitle.of("일정 제목"))
                .place(Place.of("장소 식별자", "장소명", Coordinate.of(23.21, 23.24)))
                .scheduleIndex(ScheduleIndex.ZERO_INDEX)
                .build();
        rediscoveredTrip.getDays().add(rediscoveredTargetDay);
        rediscoveredTargetDay.getSchedules().add(rediscoveredTargetDaySchedule);
        rediscoveredTrip.getTemporaryStorage().add(rediscoveredMoveSchedule);

        ScheduleMoveCommand moveCommand = new ScheduleMoveCommand(targetDayId, targetOrder);

        when(scheduleRepository.findByIdWithTrip(eq(scheduleId)))
                .thenReturn(Optional.of(beforeMoveSchedule))
                .thenReturn(Optional.of(rediscoveredMoveSchedule));

        when(dayRepository.findByIdWithTrip(eq(targetDayId)))
                .thenReturn(Optional.of(beforeTargetDay))
                .thenReturn(Optional.of(rediscoveredTargetDay));

        given(scheduleRepository.relocateDaySchedules(eq(tripId), eq(targetDayId))).willReturn(1);

        // when
        ScheduleMoveResult scheduleMoveResult = scheduleMoveService.moveSchedule(scheduleId, tripperId, moveCommand);

        // then
        assertThat(scheduleMoveResult.getBeforeDayId()).isEqualTo(null);
        assertThat(scheduleMoveResult.getAfterDayId()).isEqualTo(targetDayId);
        assertThat(scheduleMoveResult.isPositionChanged()).isEqualTo(true);
        verify(scheduleRepository, times(2)).findByIdWithTrip(eq(scheduleId));
        verify(dayRepository, times(2)).findByIdWithTrip(eq(targetDayId));
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

        Trip beforeTrip = Trip.builder()
                .id(tripId)
                .tripperId(tripperId)
                .tripTitle(TripTitle.of("여행 제목"))
                .status(TripStatus.DECIDED)
                .tripPeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 1)))
                .build();
        Day beforeTargetDay = Day.builder()
                .id(targetDayId)
                .tripDate(LocalDate.of(2023, 3, 1))
                .trip(beforeTrip)
                .build();
        Schedule beforeTargetDaySchedule1 = Schedule.builder()
                .id(scheduleId)
                .day(beforeTargetDay)
                .trip(beforeTrip)
                .scheduleTitle(ScheduleTitle.of("일정 제목1"))
                .place(Place.of("장소 식별자1", "장소명1", Coordinate.of(23.21, 23.24)))
                .scheduleIndex(ScheduleIndex.of(10))
                .build();
        Schedule beforeTargetDaySchedule2 = Schedule.builder()
                .id(scheduleId)
                .day(beforeTargetDay)
                .trip(beforeTrip)
                .scheduleTitle(ScheduleTitle.of("일정 제목2"))
                .place(Place.of("장소 식별자2", "장소명2", Coordinate.of(23.21, 23.24)))
                .scheduleIndex(ScheduleIndex.of(11))
                .build();
        Schedule beforeMoveSchedule = Schedule.builder()
                .id(scheduleId)
                .day(null)
                .trip(beforeTrip)
                .scheduleTitle(ScheduleTitle.of("일정 제목3"))
                .place(Place.of("장소 식별자3", "장소명3", Coordinate.of(23.21, 23.24)))
                .scheduleIndex(ScheduleIndex.ZERO_INDEX)
                .build();
        beforeTrip.getDays().add(beforeTargetDay);
        beforeTargetDay.getSchedules().add(beforeTargetDaySchedule1);
        beforeTargetDay.getSchedules().add(beforeTargetDaySchedule2);
        beforeTrip.getTemporaryStorage().add(beforeMoveSchedule);

        Trip rediscoveredTrip = Trip.builder()
                .id(tripId)
                .tripperId(tripperId)
                .tripTitle(TripTitle.of("여행 제목"))
                .status(TripStatus.DECIDED)
                .tripPeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 1)))
                .build();
        Day rediscoveredTargetDay = Day.builder()
                .id(targetDayId)
                .tripDate(LocalDate.of(2023, 3, 1))
                .trip(rediscoveredTrip)
                .build();
        Schedule rediscoveredTargetDaySchedule1 = Schedule.builder()
                .id(scheduleId)
                .day(rediscoveredTargetDay)
                .trip(rediscoveredTrip)
                .scheduleTitle(ScheduleTitle.of("일정 제목1"))
                .place(Place.of("장소 식별자1", "장소명1", Coordinate.of(23.21, 23.24)))
                .scheduleIndex(ScheduleIndex.ZERO_INDEX)
                .build();
        Schedule rediscoveredTargetDaySchedule2 = Schedule.builder()
                .id(scheduleId)
                .day(rediscoveredTargetDay)
                .trip(rediscoveredTrip)
                .scheduleTitle(ScheduleTitle.of("일정 제목2"))
                .place(Place.of("장소 식별자2", "장소명2", Coordinate.of(23.21, 23.24)))
                .scheduleIndex(ScheduleIndex.of(ScheduleIndex.DEFAULT_SEQUENCE_GAP))
                .build();
        Schedule rediscoveredMoveSchedule = Schedule.builder()
                .id(scheduleId)
                .day(null)
                .trip(rediscoveredTrip)
                .scheduleTitle(ScheduleTitle.of("일정 제목"))
                .place(Place.of("장소 식별자", "장소명", Coordinate.of(23.21, 23.24)))
                .scheduleIndex(ScheduleIndex.ZERO_INDEX)
                .build();
        rediscoveredTrip.getDays().add(rediscoveredTargetDay);
        rediscoveredTargetDay.getSchedules().add(rediscoveredTargetDaySchedule1);
        rediscoveredTargetDay.getSchedules().add(rediscoveredTargetDaySchedule2);
        rediscoveredTrip.getTemporaryStorage().add(rediscoveredMoveSchedule);

        ScheduleMoveCommand moveCommand = new ScheduleMoveCommand(targetDayId, targetOrder);

        when(scheduleRepository.findByIdWithTrip(eq(scheduleId)))
                .thenReturn(Optional.of(beforeMoveSchedule))
                .thenReturn(Optional.of(rediscoveredMoveSchedule));

        when(dayRepository.findByIdWithTrip(eq(targetDayId)))
                .thenReturn(Optional.of(beforeTargetDay))
                .thenReturn(Optional.of(rediscoveredTargetDay));

        given(scheduleRepository.relocateDaySchedules(eq(tripId), eq(targetDayId))).willReturn(1);

        // when
        ScheduleMoveResult scheduleMoveResult = scheduleMoveService.moveSchedule(scheduleId, tripperId, moveCommand);

        // then
        assertThat(scheduleMoveResult.getBeforeDayId()).isEqualTo(null);
        assertThat(scheduleMoveResult.getAfterDayId()).isEqualTo(targetDayId);
        assertThat(scheduleMoveResult.isPositionChanged()).isEqualTo(true);
        verify(scheduleRepository, times(2)).findByIdWithTrip(eq(scheduleId));
        verify(dayRepository, times(2)).findByIdWithTrip(eq(targetDayId));
        verify(scheduleRepository, times(1)).relocateDaySchedules(eq(tripId), eq(targetDayId));
    }
}
