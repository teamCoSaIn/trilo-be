package com.cosain.trilo.unit.trip.application.schedule.service.schedule_update;

import com.cosain.trilo.fixture.ScheduleFixture;
import com.cosain.trilo.fixture.TripFixture;
import com.cosain.trilo.trip.application.exception.NoScheduleUpdateAuthorityException;
import com.cosain.trilo.trip.application.exception.ScheduleNotFoundException;
import com.cosain.trilo.trip.application.schedule.service.schedule_update.ScheduleUpdateCommand;
import com.cosain.trilo.trip.application.schedule.service.schedule_update.ScheduleUpdateService;
import com.cosain.trilo.trip.domain.entity.Schedule;
import com.cosain.trilo.trip.domain.entity.Trip;
import com.cosain.trilo.trip.domain.repository.ScheduleRepository;
import com.cosain.trilo.trip.domain.vo.ScheduleContent;
import com.cosain.trilo.trip.domain.vo.ScheduleTime;
import com.cosain.trilo.trip.domain.vo.ScheduleTitle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ScheduleUpdateServiceTest {

    @InjectMocks
    private ScheduleUpdateService scheduleUpdateService;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Test
    @DisplayName("일정 수정(제목, 본문, 시간) 요청 -> 수정 성공")
    public void update_schedule_test(){
        // given
        long scheduleId = 1L;
        long requestTripperId = 2L;

        String rawScheduleTitle = "수정 일정 제목";
        String rawScheduleContent = "수정 일정 본문";
        LocalTime startTime = LocalTime.of(13, 0);
        LocalTime endTime = LocalTime.of(13, 5);

        var command = ScheduleUpdateCommand.of(scheduleId, requestTripperId, rawScheduleTitle, rawScheduleContent, startTime, endTime);

        long tripId = 3L;
        Trip trip = TripFixture.undecided_Id(tripId, requestTripperId);
        Schedule schedule = ScheduleFixture.temporaryStorage_Id(scheduleId, trip, 0L);

        given(scheduleRepository.findByIdWithTrip(anyLong())).willReturn(Optional.of(schedule));
        // when
        scheduleUpdateService.updateSchedule(command);

        // then
        verify(scheduleRepository, times(1)).findByIdWithTrip(eq(scheduleId));
        assertThat(schedule.getScheduleTitle()).isEqualTo(ScheduleTitle.of(rawScheduleTitle));
        assertThat(schedule.getScheduleContent()).isEqualTo(ScheduleContent.of(rawScheduleContent));
        assertThat(schedule.getScheduleTime()).isEqualTo(ScheduleTime.of(startTime, endTime));
    }

    @Test
    @DisplayName("요청한 일정이 존재하지 않는다면, ScheduleNotFoundException 이 발생한다")
    public void when_no_schedule_test(){
        // given
        long scheduleId = 1L;
        long requestTripperId = 2L;

        String rawScheduleTitle = "수정 일정 제목";
        String rawScheduleContent = "수정 일정 본문";
        LocalTime startTime = LocalTime.of(13, 0);
        LocalTime endTime = LocalTime.of(13, 5);

        ScheduleUpdateCommand command = ScheduleUpdateCommand.of(scheduleId, requestTripperId, rawScheduleTitle, rawScheduleContent, startTime, endTime);

        given(scheduleRepository.findByIdWithTrip(eq(scheduleId))).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> scheduleUpdateService.updateSchedule(command))
                .isInstanceOf(ScheduleNotFoundException.class);
    }

    @Test
    @DisplayName("권한이 없는 사람이 Schedule 을 변경하려고 하면, NoScheduleUpdateAuthorityException 이 발생한다")
    public void when_no_authority_user_try_update_test(){
        // given
        long scheduleId = 1L;
        long noAuthorityTripperId = 2L;

        String rawScheduleTitle = "수정 일정 제목";
        String rawScheduleContent = "수정 일정 본문";
        LocalTime startTime = LocalTime.of(13, 0);
        LocalTime endTime = LocalTime.of(13, 5);
        var command = ScheduleUpdateCommand.of(scheduleId, noAuthorityTripperId, rawScheduleTitle, rawScheduleContent, startTime, endTime);

        long tripId = 3L;
        long tripOwnerId = 4L;
        Trip trip = TripFixture.undecided_Id(tripId, tripOwnerId);
        Schedule schedule = ScheduleFixture.temporaryStorage_Id(scheduleId, trip, 0L);
        given(scheduleRepository.findByIdWithTrip(eq(scheduleId))).willReturn(Optional.of(schedule));

        // when & then
        assertThatThrownBy(() -> scheduleUpdateService.updateSchedule(command))
                .isInstanceOf(NoScheduleUpdateAuthorityException.class);
    }
}
