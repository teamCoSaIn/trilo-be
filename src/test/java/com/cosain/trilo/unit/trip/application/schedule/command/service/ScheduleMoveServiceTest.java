package com.cosain.trilo.unit.trip.application.schedule.command.service;

import com.cosain.trilo.fixture.TripFixture;
import com.cosain.trilo.trip.application.exception.DayNotFoundException;
import com.cosain.trilo.trip.application.exception.NoScheduleMoveAuthorityException;
import com.cosain.trilo.trip.application.exception.ScheduleNotFoundException;
import com.cosain.trilo.trip.application.exception.TooManyDayScheduleException;
import com.cosain.trilo.trip.application.schedule.command.service.ScheduleMoveService;
import com.cosain.trilo.trip.application.schedule.command.usecase.dto.ScheduleMoveCommand;
import com.cosain.trilo.trip.application.schedule.command.usecase.dto.ScheduleMoveResult;
import com.cosain.trilo.trip.domain.entity.Day;
import com.cosain.trilo.trip.domain.entity.Schedule;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.repository.DayRepository;
import com.cosain.trilo.trip.domain.repository.ScheduleRepository;
import com.cosain.trilo.trip.domain.vo.Coordinate;
import com.cosain.trilo.trip.domain.vo.Place;
import com.cosain.trilo.trip.domain.vo.ScheduleIndex;
import com.cosain.trilo.trip.domain.vo.ScheduleTitle;
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
        Long targetDayId = 2L;
        int targetOrder = 3;
        Long tripId = 1L;

        Trip trip = TripFixture.undecided_Id(tripId, tripperId);

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
        given(dayRepository.findByIdWithTrip(eq(targetDayId))).willReturn(Optional.empty()); // targetDayId에 해당하는 Day가 없음


        // when & then
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

        LocalDate startDate = LocalDate.of(2023,3,1);
        LocalDate endDate = LocalDate.of(2023,3,1);
        Trip trip = TripFixture.decided_Id(tripId, tripperId, startDate, endDate, targetDayId);
        Day day = trip.getDays().get(0);

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

        Trip trip = TripFixture.decided_Id(tripId, tripperId, startDate, endDate, targetDayId);
        Day day = trip.getDays().get(0);

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
        given(scheduleRepository.findDayScheduleCount(eq(targetDayId))).willReturn(0);

        // when
        ScheduleMoveResult scheduleMoveResult = scheduleMoveService.moveSchedule(scheduleId, tripperId, moveCommand);


        // then
        assertThat(scheduleMoveResult.getBeforeDayId()).isEqualTo(null);
        assertThat(scheduleMoveResult.getAfterDayId()).isEqualTo(targetDayId);
        assertThat(scheduleMoveResult.isPositionChanged()).isEqualTo(true);
        verify(scheduleRepository, times(1)).findByIdWithTrip(eq(scheduleId));
        verify(dayRepository, times(1)).findByIdWithTrip(eq(targetDayId));
        verify(scheduleRepository, times(1)).findDayScheduleCount(eq(targetDayId));
        verify(scheduleRepository, times(0)).relocateDaySchedules(eq(tripId), eq(targetDayId));
    }

    @DisplayName("임시보관함 -> 임시보관함 성공 테스트")
    @Test
    public void test_temporaryStorage_to_temporaryStorage_success() {
        // given
        Long tripId = 1L;
        Long scheduleId = 2L;
        Long tripperId = 3L;

        Long beforeDayId = null;
        Long targetDayId = null;
        int targetOrder = 2;

        Trip trip = TripFixture.undecided_Id(tripId, tripperId);

        Schedule schedule1 = Schedule.builder()
                .id(scheduleId)
                .day(null)
                .trip(trip)
                .scheduleTitle(ScheduleTitle.of("일정 제목1"))
                .place(Place.of("장소 식별자", "장소명", Coordinate.of(23.21, 23.24)))
                .scheduleIndex(ScheduleIndex.ZERO_INDEX)
                .build();

        Schedule schedule2 = Schedule.builder()
                .id(scheduleId)
                .day(null)
                .trip(trip)
                .scheduleTitle(ScheduleTitle.of("일정 제목2"))
                .place(Place.of("장소 식별자2", "장소명2", Coordinate.of(23.21, 23.24)))
                .scheduleIndex(ScheduleIndex.of(ScheduleIndex.DEFAULT_SEQUENCE_GAP))
                .build();

        Schedule schedule3 = Schedule.builder()
                .id(scheduleId)
                .day(null)
                .trip(trip)
                .scheduleTitle(ScheduleTitle.of("일정 제목3"))
                .place(Place.of("장소 식별자3", "장소명3", Coordinate.of(23.21, 23.24)))
                .scheduleIndex(ScheduleIndex.of(ScheduleIndex.DEFAULT_SEQUENCE_GAP * 2))
                .build();

        trip.getTemporaryStorage().add(schedule1);
        trip.getTemporaryStorage().add(schedule2);
        trip.getTemporaryStorage().add(schedule3);


        ScheduleMoveCommand moveCommand = new ScheduleMoveCommand(targetDayId, targetOrder);

        given(scheduleRepository.findByIdWithTrip(eq(scheduleId))).willReturn(Optional.of(schedule1));

        // when
        ScheduleMoveResult scheduleMoveResult = scheduleMoveService.moveSchedule(scheduleId, tripperId, moveCommand);

        // then
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
        Long scheduleId = 2L;
        Long tripperId = 3L;

        Long fromDayId = 4L;
        Long targetDayId = null;
        int targetOrder = 2;

        LocalDate startDate = LocalDate.of(2023,3,1);
        LocalDate endDate = LocalDate.of(2023,3,1);

        Trip trip = TripFixture.decided_Id(tripId, tripperId, startDate, endDate, fromDayId);
        Day fromDay = trip.getDays().get(0);

        Schedule schedule1 = Schedule.builder()
                .id(scheduleId)
                .day(fromDay)
                .trip(trip)
                .scheduleTitle(ScheduleTitle.of("일정 제목1"))
                .place(Place.of("장소 식별자", "장소명", Coordinate.of(23.21, 23.24)))
                .scheduleIndex(ScheduleIndex.ZERO_INDEX)
                .build();

        Schedule schedule2 = Schedule.builder()
                .id(scheduleId)
                .day(null)
                .trip(trip)
                .scheduleTitle(ScheduleTitle.of("일정 제목2"))
                .place(Place.of("장소 식별자2", "장소명2", Coordinate.of(23.21, 23.24)))
                .scheduleIndex(ScheduleIndex.ZERO_INDEX)
                .build();

        Schedule schedule3 = Schedule.builder()
                .id(scheduleId)
                .day(null)
                .trip(trip)
                .scheduleTitle(ScheduleTitle.of("일정 제목3"))
                .place(Place.of("장소 식별자3", "장소명3", Coordinate.of(23.21, 23.24)))
                .scheduleIndex(ScheduleIndex.of(ScheduleIndex.DEFAULT_SEQUENCE_GAP))
                .build();

        fromDay.getSchedules().add(schedule1);
        trip.getTemporaryStorage().add(schedule2);
        trip.getTemporaryStorage().add(schedule3);

        ScheduleMoveCommand moveCommand = new ScheduleMoveCommand(targetDayId, targetOrder);

        given(scheduleRepository.findByIdWithTrip(eq(scheduleId))).willReturn(Optional.of(schedule1));

        // when
        ScheduleMoveResult scheduleMoveResult = scheduleMoveService.moveSchedule(scheduleId, tripperId, moveCommand);

        // then
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
        Long scheduleId = 2L;
        Long tripperId = 3L;

        Long targetDayId = 4L;
        int targetOrder = 2;

        LocalDate startDate = LocalDate.of(2023,3,1);
        LocalDate endDate = LocalDate.of(2023,3,1);

        Trip trip = TripFixture.decided_Id(tripId, tripperId, startDate, endDate, targetDayId);
        Day day = trip.getDays().get(0);

        Schedule schedule1 = Schedule.builder()
                .id(scheduleId)
                .day(day)
                .trip(trip)
                .scheduleTitle(ScheduleTitle.of("일정 제목1"))
                .place(Place.of("장소 식별자", "장소명", Coordinate.of(23.21, 23.24)))
                .scheduleIndex(ScheduleIndex.ZERO_INDEX)
                .build();

        Schedule schedule2 = Schedule.builder()
                .id(scheduleId)
                .day(day)
                .trip(trip)
                .scheduleTitle(ScheduleTitle.of("일정 제목2"))
                .place(Place.of("장소 식별자2", "장소명2", Coordinate.of(23.21, 23.24)))
                .scheduleIndex(ScheduleIndex.of(ScheduleIndex.DEFAULT_SEQUENCE_GAP))
                .build();

        Schedule schedule3 = Schedule.builder()
                .id(scheduleId)
                .day(day)
                .trip(trip)
                .scheduleTitle(ScheduleTitle.of("일정 제목3"))
                .place(Place.of("장소 식별자3", "장소명3", Coordinate.of(23.21, 23.24)))
                .scheduleIndex(ScheduleIndex.of(ScheduleIndex.DEFAULT_SEQUENCE_GAP * 2))
                .build();

        day.getSchedules().add(schedule1);
        day.getSchedules().add(schedule2);
        day.getSchedules().add(schedule3);


        ScheduleMoveCommand moveCommand = new ScheduleMoveCommand(targetDayId, targetOrder);

        given(scheduleRepository.findByIdWithTrip(eq(scheduleId))).willReturn(Optional.of(schedule1));
        given(dayRepository.findByIdWithTrip(eq(targetDayId))).willReturn(Optional.of(day));

        // when
        ScheduleMoveResult scheduleMoveResult = scheduleMoveService.moveSchedule(scheduleId, tripperId, moveCommand);

        // then
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
        Long scheduleId = 2L;
        Long tripperId = 3L;

        Long fromDayId = 4L;
        Long targetDayId = 5L;
        int targetOrder = 2;

        LocalDate startDate = LocalDate.of(2023,3,1);
        LocalDate endDate = LocalDate.of(2023,3,2);

        Trip trip = TripFixture.decided_Id(tripId, tripperId, startDate, endDate, fromDayId);
        Day fromDay = trip.getDays().get(0);
        Day targetDay = trip.getDays().get(1);

        Schedule schedule1 = Schedule.builder()
                .id(scheduleId)
                .day(fromDay)
                .trip(trip)
                .scheduleTitle(ScheduleTitle.of("일정 제목"))
                .place(Place.of("장소 식별자", "장소명", Coordinate.of(23.21, 23.24)))
                .scheduleIndex(ScheduleIndex.ZERO_INDEX)
                .build();

        Schedule schedule2 = Schedule.builder()
                .id(scheduleId)
                .day(targetDay)
                .trip(trip)
                .scheduleTitle(ScheduleTitle.of("일정 제목2"))
                .place(Place.of("장소 식별자2", "장소명2", Coordinate.of(23.21, 23.24)))
                .scheduleIndex(ScheduleIndex.of(ScheduleIndex.DEFAULT_SEQUENCE_GAP))
                .build();

        Schedule schedule3 = Schedule.builder()
                .id(scheduleId)
                .day(targetDay)
                .trip(trip)
                .scheduleTitle(ScheduleTitle.of("일정 제목3"))
                .place(Place.of("장소 식별자3", "장소명3", Coordinate.of(23.21, 23.24)))
                .scheduleIndex(ScheduleIndex.of(ScheduleIndex.DEFAULT_SEQUENCE_GAP * 2))
                .build();

        fromDay.getSchedules().add(schedule1);
        targetDay.getSchedules().add(schedule2);
        targetDay.getSchedules().add(schedule3);

        ScheduleMoveCommand moveCommand = new ScheduleMoveCommand(targetDayId, targetOrder);

        given(scheduleRepository.findByIdWithTrip(eq(scheduleId))).willReturn(Optional.of(schedule1));
        given(dayRepository.findByIdWithTrip(eq(targetDayId))).willReturn(Optional.of(targetDay));
        given(scheduleRepository.findDayScheduleCount(eq(targetDayId))).willReturn(2);

        // when
        ScheduleMoveResult scheduleMoveResult = scheduleMoveService.moveSchedule(scheduleId, tripperId, moveCommand);


        // then
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
        Long scheduleId = 2L;
        Long tripperId = 3L;

        Long targetDayId = 4L;
        int targetOrder = 1;

        LocalDate startDate = LocalDate.of(2023,3,1);
        LocalDate endDate = LocalDate.of(2023,3,1);

        Trip beforeTrip = TripFixture.decided_Id(tripId, tripperId, startDate, endDate, targetDayId);
        Day beforeTargetDay = beforeTrip.getDays().get(0);

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
        beforeTargetDay.getSchedules().add(beforeTargetDaySchedule);
        beforeTrip.getTemporaryStorage().add(beforeMoveSchedule);

        Trip rediscoveredTrip = TripFixture.decided_Id(tripId, tripperId, startDate, endDate, targetDayId);
        Day rediscoveredTargetDay = rediscoveredTrip.getDays().get(0);

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
        given(scheduleRepository.findDayScheduleCount(eq(targetDayId))).willReturn(1);


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

    @DisplayName("맨 앞에 이동 시 인덱스 범위 벗어나면 재배치 후 리포지토리 호출이 추가적으로 발생")
    @Test
    public void testRelocate_whenMoveToHead() {
        // given
        Long tripId = 1L;
        Long scheduleId = 2L;
        Long tripperId = 3L;

        Long targetDayId = 4L;
        int targetOrder = 0;

        LocalDate startDate = LocalDate.of(2023,3,1);
        LocalDate endDate = LocalDate.of(2023,3,1);

        Trip beforeTrip = TripFixture.decided_Id(tripId, tripperId, startDate, endDate, targetDayId);
        Day beforeTargetDay = beforeTrip.getDays().get(0);
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
        beforeTargetDay.getSchedules().add(beforeTargetDaySchedule);
        beforeTrip.getTemporaryStorage().add(beforeMoveSchedule);

        Trip rediscoveredTrip = TripFixture.decided_Id(tripId, tripperId, startDate, endDate, targetDayId);
        Day rediscoveredTargetDay = rediscoveredTrip.getDays().get(0);
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
        rediscoveredTargetDay.getSchedules().add(rediscoveredTargetDaySchedule);
        rediscoveredTrip.getTemporaryStorage().add(rediscoveredMoveSchedule);

        ScheduleMoveCommand moveCommand = new ScheduleMoveCommand(targetDayId, targetOrder);

        when(scheduleRepository.findByIdWithTrip(eq(scheduleId)))
                .thenReturn(Optional.of(beforeMoveSchedule))
                .thenReturn(Optional.of(rediscoveredMoveSchedule));

        when(dayRepository.findByIdWithTrip(eq(targetDayId)))
                .thenReturn(Optional.of(beforeTargetDay))
                .thenReturn(Optional.of(rediscoveredTargetDay));

        given(scheduleRepository.findDayScheduleCount(eq(targetDayId))).willReturn(1);
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

    @DisplayName("중간 이동 시 충돌나면 재배치 후 리포지토리 호출이 추가적으로 발생")
    @Test
    public void testRelocate_whenMoveToMiddle() {
        // given
        Long tripId = 1L;
        Long scheduleId = 2L;
        Long tripperId = 3L;

        Long targetDayId = 4L;
        int targetOrder = 1;

        LocalDate startDate = LocalDate.of(2023,3,1);
        LocalDate endDate = LocalDate.of(2023,3,1);

        Trip beforeTrip = TripFixture.decided_Id(tripId, tripperId, startDate, endDate, targetDayId);
        Day beforeTargetDay = beforeTrip.getDays().get(0);

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
        beforeTargetDay.getSchedules().add(beforeTargetDaySchedule1);
        beforeTargetDay.getSchedules().add(beforeTargetDaySchedule2);
        beforeTrip.getTemporaryStorage().add(beforeMoveSchedule);

        Trip rediscoveredTrip = TripFixture.decided_Id(tripId, tripperId, startDate, endDate, targetDayId);
        Day rediscoveredTargetDay = rediscoveredTrip.getDays().get(0);
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

        LocalDate startDate = LocalDate.of(2023,3,1);
        LocalDate endDate = LocalDate.of(2023,3,1);

        Trip trip = TripFixture.decided_Id(tripId, tripperId, startDate, endDate, targetDayId);
        Day targetDay = trip.getDays().get(0);

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
        given(dayRepository.findByIdWithTrip(eq(targetDayId))).willReturn(Optional.of(targetDay));

        // targetDay에 일정이 가득찬 상황을 가정
        given(scheduleRepository.findDayScheduleCount(eq(targetDayId))).willReturn(Day.MAX_DAY_SCHEDULE_COUNT);

        // when
        assertThatThrownBy(() -> scheduleMoveService.moveSchedule(scheduleId, tripperId, moveCommand))
                .isInstanceOf(TooManyDayScheduleException.class);

        // then
        verify(scheduleRepository, times(1)).findByIdWithTrip(eq(scheduleId));
        verify(dayRepository, times(1)).findByIdWithTrip(eq(targetDayId));
        verify(scheduleRepository, times(1)).findDayScheduleCount(eq(targetDayId));
        verify(scheduleRepository, times(0)).relocateDaySchedules(eq(tripId), eq(targetDayId));
    }

}
