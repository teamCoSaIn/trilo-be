package com.cosain.trilo.unit.trip.command.application.service.schedule;

import com.cosain.trilo.trip.command.application.command.ScheduleCreateCommand;
import com.cosain.trilo.trip.command.application.exception.NoScheduleCreateAuthorityException;
import com.cosain.trilo.trip.command.application.service.ScheduleCreateService;
import com.cosain.trilo.trip.command.domain.entity.Day;
import com.cosain.trilo.trip.command.domain.entity.Schedule;
import com.cosain.trilo.trip.command.domain.entity.Trip;
import com.cosain.trilo.trip.command.domain.repository.DayRepository;
import com.cosain.trilo.trip.command.domain.repository.ScheduleRepository;
import com.cosain.trilo.trip.command.domain.repository.TripRepository;
import com.cosain.trilo.trip.command.domain.vo.Coordinate;
import com.cosain.trilo.trip.command.domain.vo.Place;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

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

    @Test
    @DisplayName("호출이 제대로 이루어지는지 테스트")
    public void create_schedule_called_test(){
        // given
        Long tripperId = 1L;
        Trip trip = Trip.create("제목", tripperId);
        Day day = Day.of(LocalDate.of(2023, 4, 5), trip);
        Schedule schedule = Schedule.create(day, trip, "제목", Place.of("장소 식별자", "장소 이름", Coordinate.of(23.21, 23.24)));

        ScheduleCreateCommand scheduleCreateCommand = ScheduleCreateCommand.of(1L, 1L, "제목", "내용", "장소 식별자", 23.21, 23.24);
        given(scheduleRepository.save(any(Schedule.class))).willReturn(schedule);
        given(dayRepository.findById(anyLong())).willReturn(Optional.of(day));
        given(tripRepository.findById(anyLong())).willReturn(Optional.of(trip));

        // when
        scheduleCreateService.createSchedule(tripperId, scheduleCreateCommand);

        // then
        verify(scheduleRepository).save(any());
        verify(dayRepository).findById(anyLong());
        verify(tripRepository).findById(anyLong());
    }

    @Test
    @DisplayName("권한 없는 사람이 Schedule을 생성하면, NoScheduleCreateAuthortyException이 발생한다.")
    public void when_no_authority_tripper_create_schedule_it_throws_NoScheduleCreateAuthorityException(){
        // given
        Long noAuthorityTripperId = 2L;

        Trip trip = Trip.create("제목", 1L);
        Day day = Day.of(LocalDate.of(2023, 4, 5), trip);

        ScheduleCreateCommand scheduleCreateCommand = ScheduleCreateCommand.of(1L, 1L, "제목", "내용", "장소 식별자", 23.21, 23.24);
        given(dayRepository.findById(anyLong())).willReturn(Optional.of(day));
        given(tripRepository.findById(anyLong())).willReturn(Optional.of(trip));

        // when & then
        assertThatThrownBy(() -> scheduleCreateService.createSchedule(noAuthorityTripperId, scheduleCreateCommand))
                .isInstanceOf(NoScheduleCreateAuthorityException.class);
        verify(dayRepository).findById(anyLong());
        verify(tripRepository).findById(anyLong());
    }

}
