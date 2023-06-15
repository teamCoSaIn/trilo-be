package com.cosain.trilo.unit.trip.application.schedule.command.service;

import com.cosain.trilo.fixture.TripFixture;
import com.cosain.trilo.trip.application.exception.TooManyDayScheduleException;
import com.cosain.trilo.trip.application.exception.TooManyTripScheduleException;
import com.cosain.trilo.trip.application.schedule.command.usecase.dto.ScheduleCreateCommand;
import com.cosain.trilo.trip.application.exception.NoScheduleCreateAuthorityException;
import com.cosain.trilo.trip.application.schedule.command.service.ScheduleCreateService;
import com.cosain.trilo.trip.domain.entity.Day;
import com.cosain.trilo.trip.domain.entity.Schedule;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.repository.DayRepository;
import com.cosain.trilo.trip.domain.repository.ScheduleRepository;
import com.cosain.trilo.trip.domain.repository.TripRepository;
import com.cosain.trilo.trip.domain.vo.*;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScheduleCreateServiceTest {

    @InjectMocks
    private ScheduleCreateService scheduleCreateService;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private DayRepository dayRepository;

    @Mock
    private TripRepository tripRepository;

    @Nested
    @DisplayName("Day에 일정을 생성할 때")
    class Case_CreateDaySchedule {

        @Test
        @DisplayName("범위를 넘기지 않을 때, 리포지토리 호출이 한번씩만 이루어지는지 테스트")
        public void when_there_is_no_scheduleIndexRangeException_repository_is_called_only_once() {
            // given
            Long tripperId = 1L;
            Long tripId = 2L;
            Long dayId = 3L;

            Trip trip = Trip.builder()
                    .id(tripId)
                    .tripperId(tripperId)
                    .tripTitle(TripTitle.of("여행 제목"))
                    .status(TripStatus.DECIDED)
                    .tripPeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 1)))
                    .build();

            Day day = Day.builder()
                    .id(dayId)
                    .tripDate(LocalDate.of(2023, 3, 1))
                    .trip(trip)
                    .build();

            trip.getDays().add(day);

            ScheduleCreateCommand scheduleCreateCommand = ScheduleCreateCommand.builder()
                    .dayId(dayId)
                    .tripId(tripId)
                    .scheduleTitle(ScheduleTitle.of("일정 제목"))
                    .place(Place.of("장소식별자", "장소명", Coordinate.of(23.21, 23.24)))
                    .build();

            Schedule createdSchedule = Schedule.builder()
                    .id(1L)
                    .day(day)
                    .trip(trip)
                    .scheduleTitle(ScheduleTitle.of("일정 제목"))
                    .place(Place.of("장소 식별자", "장소명", Coordinate.of(23.21, 23.24)))
                    .scheduleIndex(ScheduleIndex.ZERO_INDEX)
                    .build();

            given(dayRepository.findByIdWithTrip(eq(dayId))).willReturn(Optional.of(day));
            given(tripRepository.findById(eq(tripId))).willReturn(Optional.of(trip));
            given(scheduleRepository.save(any(Schedule.class))).willReturn(createdSchedule);
            given(scheduleRepository.findTripScheduleCount(eq(tripId))).willReturn(0);
            given(scheduleRepository.findDayScheduleCount(eq(dayId))).willReturn(0);

            // when
            scheduleCreateService.createSchedule(tripperId, scheduleCreateCommand);

            // then
            verify(dayRepository, times(1)).findByIdWithTrip(eq(dayId));
            verify(tripRepository, times(1)).findById(eq(tripId));
            verify(scheduleRepository, times(0)).relocateDaySchedules(eq(tripId), eq(dayId));
            verify(scheduleRepository, times(1)).save(any(Schedule.class));
            verify(scheduleRepository, times(1)).findTripScheduleCount(eq(tripId));
            verify(scheduleRepository, times(1)).findDayScheduleCount(eq(dayId));
        }

        @Test
        @DisplayName("범위를 넘길 때, 리포지토리 호출이 한번씩만 이루어지는지 테스트")
        public void when_day_scheduleIndex_is_over_limit_then_relocate_called() {
            // given
            Long tripperId = 1L;
            Long tripId = 2L;
            Long dayId = 3L;

            Trip beforeTrip = Trip.builder()
                    .id(tripId)
                    .tripperId(tripperId)
                    .tripTitle(TripTitle.of("여행 제목"))
                    .status(TripStatus.DECIDED)
                    .tripPeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 1)))
                    .build();

            Day beforeDay = Day.builder()
                    .id(dayId)
                    .tripDate(LocalDate.of(2023, 3, 1))
                    .trip(beforeTrip)
                    .build();


            Schedule beforeSchedule = Schedule.builder()
                    .id(1L)
                    .day(beforeDay)
                    .trip(beforeTrip)
                    .scheduleTitle(ScheduleTitle.of("제목"))
                    .place(Place.of("장소식별자", "장소명", Coordinate.of(23.21, 23.24)))
                    .scheduleIndex(ScheduleIndex.of(ScheduleIndex.MAX_INDEX_VALUE))
                    .build();
            beforeDay.getSchedules().add(beforeSchedule);
            beforeTrip.getDays().add(beforeDay);

            ScheduleCreateCommand scheduleCreateCommand = ScheduleCreateCommand.builder()
                    .dayId(dayId)
                    .tripId(tripId)
                    .scheduleTitle(ScheduleTitle.of("일정 제목"))
                    .place(Place.of("장소 식별자", "장소명", Coordinate.of(23.21, 23.24)))
                    .build();


            Trip rediscoveredTrip = Trip.builder()
                    .id(tripId)
                    .tripperId(tripperId)
                    .tripTitle(TripTitle.of("여행 제목"))
                    .status(TripStatus.DECIDED)
                    .tripPeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 1)))
                    .build();

            Day rediscoveredDay = Day.builder()
                    .id(dayId)
                    .tripDate(LocalDate.of(2023, 3, 1))
                    .trip(rediscoveredTrip)
                    .build();

            Schedule rediscoveredBeforeSchedule = Schedule.builder()
                    .id(1L)
                    .day(rediscoveredDay)
                    .trip(rediscoveredTrip)
                    .scheduleTitle(ScheduleTitle.of("제목"))
                    .place(Place.of("장소식별자", "장소명", Coordinate.of(23.21, 23.24)))
                    .scheduleIndex(ScheduleIndex.ZERO_INDEX)
                    .build();

            rediscoveredDay.getSchedules().add(rediscoveredBeforeSchedule);
            rediscoveredTrip.getDays().add(rediscoveredDay);

            Schedule createdSchedule = Schedule.builder()
                    .id(2L)
                    .day(rediscoveredDay)
                    .trip(rediscoveredTrip)
                    .scheduleTitle(ScheduleTitle.of("일정 제목"))
                    .place(Place.of("장소 식별자", "장소명", Coordinate.of(23.21, 23.24)))
                    .scheduleIndex(ScheduleIndex.of(ScheduleIndex.DEFAULT_SEQUENCE_GAP))
                    .build();

            when(dayRepository.findByIdWithTrip(eq(dayId)))
                    .thenReturn(Optional.of(beforeDay))
                    .thenReturn(Optional.of(rediscoveredDay));

            when(tripRepository.findById(eq(tripId)))
                    .thenReturn(Optional.of(beforeTrip))
                    .thenReturn(Optional.of(rediscoveredTrip));

            given(scheduleRepository.relocateDaySchedules(eq(tripId), eq(dayId))).willReturn(1);
            given(scheduleRepository.save(any(Schedule.class))).willReturn(createdSchedule);
            given(scheduleRepository.findTripScheduleCount(eq(tripId))).willReturn(1);
            given(scheduleRepository.findDayScheduleCount(eq(dayId))).willReturn(1);

            // when
            scheduleCreateService.createSchedule(tripperId, scheduleCreateCommand);

            // then
            verify(dayRepository, times(2)).findByIdWithTrip(eq(dayId));
            verify(tripRepository, times(2)).findById(eq(tripId));
            verify(scheduleRepository, times(1)).relocateDaySchedules(eq(tripId), eq(dayId));
            verify(scheduleRepository, times(1)).save(any(Schedule.class));
            verify(scheduleRepository, times(1)).findTripScheduleCount(eq(tripId));
            verify(scheduleRepository, times(1)).findDayScheduleCount(eq(dayId));
        }
    }

    @Nested
    @DisplayName("임시보관함에 일정을 생성할 때")
    class Case_Create_To_TemporaryStorage {

        @Test
        @DisplayName("범위를 넘기지 않을 때, 리포지토리 호출이 한번씩만 이루어지는지 테스트")
        public void when_there_is_no_scheduleIndexRangeException_repository_is_called_only_once() {
            // given
            Long tripperId = 1L;
            Long tripId = 2L;
            Long dayId = null;

            Trip trip = Trip.builder()
                    .id(tripId)
                    .tripperId(tripperId)
                    .tripTitle(TripTitle.of("여행 제목"))
                    .status(TripStatus.DECIDED)
                    .tripPeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 1)))
                    .build();

            ScheduleCreateCommand scheduleCreateCommand = ScheduleCreateCommand.builder()
                    .dayId(dayId)
                    .tripId(tripId)
                    .scheduleTitle(ScheduleTitle.of("일정 제목"))
                    .place(Place.of("장소 식별자", "장소명", Coordinate.of(23.21, 23.24)))
                    .build();

            Schedule createdSchedule = Schedule.builder()
                    .id(1L)
                    .day(null)
                    .trip(trip)
                    .scheduleTitle(ScheduleTitle.of("일정 제목"))
                    .place(Place.of("장소 식별자", "장소명", Coordinate.of(23.21, 23.24)))
                    .scheduleIndex(ScheduleIndex.ZERO_INDEX)
                    .build();

            given(tripRepository.findById(eq(tripId))).willReturn(Optional.of(trip));
            given(scheduleRepository.save(any(Schedule.class))).willReturn(createdSchedule);
            given(scheduleRepository.findTripScheduleCount(eq(tripId))).willReturn(0);

            // when
            scheduleCreateService.createSchedule(tripperId, scheduleCreateCommand);

            // then
            verify(dayRepository, times(0)).findByIdWithTrip(isNull());
            verify(tripRepository, times(1)).findById(eq(tripId));
            verify(scheduleRepository, times(0)).relocateDaySchedules(eq(tripId), isNull());
            verify(scheduleRepository, times(1)).save(any(Schedule.class));
            verify(scheduleRepository, times(1)).findTripScheduleCount(eq(tripId));
            verify(scheduleRepository, times(0)).findDayScheduleCount(isNull());
        }

        @Test
        @DisplayName("임시보관함에서 일정의 순서가 범위를 벗어날 경우 재배치 기능이 호출되는 지 여부 테스트")
        public void when_temporaryStorage_Schedule_is_over_limit_then_relocate_called() {
            // given
            Long tripperId = 1L;
            Long tripId = 1L;
            Trip trip = TripFixture.UNDECIDED_TRIP.createUndecided(tripId, tripperId, "제목");

            Schedule beforeSchedule = Schedule.builder()
                    .id(1L)
                    .day(null)
                    .trip(trip)
                    .scheduleTitle(ScheduleTitle.of("제목"))
                    .place(Place.of("장소식별자", "장소명", Coordinate.of(23.21, 23.24)))
                    .scheduleIndex(ScheduleIndex.of(ScheduleIndex.MAX_INDEX_VALUE))
                    .build();
            trip.getTemporaryStorage().add(beforeSchedule);

            Trip rediscoveredTrip = TripFixture.UNDECIDED_TRIP.createUndecided(tripId, tripperId, "제목");
            Schedule relocatedSchedule = Schedule.builder()
                    .id(1L)
                    .day(null)
                    .trip(trip)
                    .scheduleTitle(ScheduleTitle.of("제목"))
                    .place(Place.of("장소식별자", "장소명", Coordinate.of(23.21, 23.24)))
                    .scheduleIndex(ScheduleIndex.ZERO_INDEX)
                    .build();
            rediscoveredTrip.getTemporaryStorage().add(relocatedSchedule);

            ScheduleCreateCommand scheduleCreateCommand = ScheduleCreateCommand.builder()
                    .dayId(null)
                    .tripId(tripId)
                    .scheduleTitle(ScheduleTitle.of("일정제목2"))
                    .place(Place.of("장소식별자2", "장소이름2", Coordinate.of(19.18, 27.15)))
                    .build();

            Schedule newSchedule = Schedule.builder()
                    .id(2L)
                    .day(null)
                    .trip(trip)
                    .scheduleTitle(ScheduleTitle.of("일정제목2"))
                    .place(Place.of("장소식별자2", "장소이름2", Coordinate.of(19.18, 27.15)))
                    .scheduleIndex(ScheduleIndex.of(ScheduleIndex.DEFAULT_SEQUENCE_GAP))
                    .build();

            when(tripRepository.findById(eq(tripId)))
                    .thenReturn(Optional.of(trip))
                    .thenReturn(Optional.of(rediscoveredTrip));
            given(scheduleRepository.relocateDaySchedules(eq(tripId), isNull())).willReturn(1);
            given(scheduleRepository.save(any(Schedule.class))).willReturn(newSchedule);
            given(scheduleRepository.findTripScheduleCount(eq(tripId))).willReturn(1);

            // when
            scheduleCreateService.createSchedule(tripperId, scheduleCreateCommand);

            // then
            verify(dayRepository, times(0)).findByIdWithTrip(isNull());
            verify(tripRepository, times(2)).findById(eq(tripId));
            verify(scheduleRepository, times(1)).relocateDaySchedules(eq(tripId), isNull());
            verify(scheduleRepository, times(1)).save(any(Schedule.class));
            verify(scheduleRepository, times(1)).findTripScheduleCount(eq(tripId));
            verify(scheduleRepository, times(0)).findDayScheduleCount(isNull());
        }

    }

    @Test
    @DisplayName("권한 없는 사람이 Schedule을 생성하면, NoScheduleCreateAuthortyException이 발생한다.")
    public void when_no_authority_tripper_create_schedule_it_throws_NoScheduleCreateAuthorityException() {
        // given
        Long tripOwnerId = 1L;
        Long noAuthorityTripperId = 2L;
        Long tripId = 3L;
        Long dayId = 4L;

        Trip trip = TripFixture.DECIDED_TRIP.createDecided(tripId, tripOwnerId, "제목", LocalDate.of(2023,4,1), LocalDate.of(2023,4,1));
        Day day = Day.builder()
                .id(dayId)
                .tripDate(LocalDate.of(2023,4,1))
                .dayColor(DayColor.BLACK)
                .build();

        ScheduleCreateCommand scheduleCreateCommand = ScheduleCreateCommand.builder()
                .dayId(dayId)
                .tripId(tripId)
                .scheduleTitle(ScheduleTitle.of("제목"))
                .place(Place.of("장소 식별자", "장소명", Coordinate.of(23.21, 23.24)))
                .build();

        given(dayRepository.findByIdWithTrip(eq(dayId))).willReturn(Optional.of(day));
        given(tripRepository.findById(eq(tripId))).willReturn(Optional.of(trip));

        // when & then
        assertThatThrownBy(() -> scheduleCreateService.createSchedule(noAuthorityTripperId, scheduleCreateCommand))
                .isInstanceOf(NoScheduleCreateAuthorityException.class);
        verify(dayRepository).findByIdWithTrip(eq(dayId));
        verify(tripRepository).findById(eq(tripId));
    }

    @Test
    @DisplayName("여행에 일정이 너무 많이 있으면, TooManyTripScheduleException이 발생한다.")
    public void tooManyTripScheduleTest() {
        // given
        Long tripperId = 1L;
        Long tripId = 2L;

        Trip trip = Trip.builder()
                .id(tripId)
                .tripperId(tripperId)
                .tripTitle(TripTitle.of("여행 제목"))
                .status(TripStatus.DECIDED)
                .tripPeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 1)))
                .build();

        ScheduleCreateCommand scheduleCreateCommand = ScheduleCreateCommand.builder()
                .dayId(null)
                .tripId(tripId)
                .scheduleTitle(ScheduleTitle.of("일정 제목"))
                .place(Place.of("장소 식별자", "장소명", Coordinate.of(23.21, 23.24)))
                .build();


        given(tripRepository.findById(eq(tripId))).willReturn(Optional.of(trip));
        given(scheduleRepository.findTripScheduleCount(eq(tripId))).willReturn(110);

        // when
        assertThatThrownBy(() -> scheduleCreateService.createSchedule(tripperId, scheduleCreateCommand))
                .isInstanceOf(TooManyTripScheduleException.class);

        // then
        verify(dayRepository, times(0)).findByIdWithTrip(isNull());
        verify(tripRepository, times(1)).findById(eq(tripId));
        verify(scheduleRepository, times(0)).relocateDaySchedules(eq(tripId), isNull());
        verify(scheduleRepository, times(0)).save(any(Schedule.class));
        verify(scheduleRepository, times(1)).findTripScheduleCount(eq(tripId));
    }

    @Test
    @DisplayName("Day에 일정이 너무 많이 있으면, TooManyDayScheduleException이 발생한다.")
    public void tooManyDayScheduleTest() {
        // given
        Long tripperId = 1L;
        Long tripId = 2L;
        Long dayId = 3L;

        Trip trip = Trip.builder()
                .id(tripId)
                .tripperId(tripperId)
                .tripTitle(TripTitle.of("여행 제목"))
                .status(TripStatus.DECIDED)
                .tripPeriod(TripPeriod.of(LocalDate.of(2023, 3, 1), LocalDate.of(2023, 3, 1)))
                .build();

        Day day = Day.builder()
                .id(dayId)
                .tripDate(LocalDate.of(2023,3,1))
                .trip(trip)
                .build();


        ScheduleCreateCommand scheduleCreateCommand = ScheduleCreateCommand.builder()
                .dayId(dayId)
                .tripId(tripId)
                .scheduleTitle(ScheduleTitle.of("일정 제목"))
                .place(Place.of("장소 식별자", "장소명", Coordinate.of(23.21, 23.24)))
                .build();


        given(tripRepository.findById(eq(tripId))).willReturn(Optional.of(trip));
        given(dayRepository.findByIdWithTrip(eq(dayId))).willReturn(Optional.of(day));
        given(scheduleRepository.findTripScheduleCount(eq(tripId))).willReturn(10);
        given(scheduleRepository.findDayScheduleCount(eq(dayId))).willReturn(10);

        // when
        assertThatThrownBy(() -> scheduleCreateService.createSchedule(tripperId, scheduleCreateCommand))
                .isInstanceOf(TooManyDayScheduleException.class);

        // then
        verify(dayRepository, times(0)).findByIdWithTrip(isNull());
        verify(tripRepository, times(1)).findById(eq(tripId));
        verify(scheduleRepository, times(0)).relocateDaySchedules(eq(tripId), isNull());
        verify(scheduleRepository, times(0)).save(any(Schedule.class));
        verify(scheduleRepository, times(1)).findTripScheduleCount(eq(tripId));
        verify(scheduleRepository, times(1)).findDayScheduleCount(eq(dayId));
    }

}
